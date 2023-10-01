package com.saadahmedev.apigateway.security;

import com.saadahmedev.apigateway.entity.User;
import com.saadahmedev.apigateway.repository.TokenRepository;
import com.saadahmedev.apigateway.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Value("${security.admin.secret-key}")
    private String adminSecretKey;

    @Value("${security.employee.secret-key}")
    private String employeeSecretKey;

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public GatewayFilter apply(JwtAuthenticationFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest.Builder customizedRequest = request.mutate();;

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return sendErrorResponse(response, "Bearer token is required");
                }

                String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authorization == null) return sendErrorResponse(response, "Bearer token is required");
                if (!authorization.startsWith("Bearer ") && authorization.split(" ")[1] == null || authorization.split(" ")[1].isEmpty()) return sendErrorResponse(response, "Bearer token is required");

                String token = authorization.substring(7);

                try {
                    if (!jwtUtil.isTokenValid(token)) return sendErrorResponse(response, "Token has been expired");
                } catch (Exception e) {
                    if (e instanceof ExpiredJwtException) {
                        tokenRepository.deleteById(token);
                        return sendErrorResponse(response, "Token has been expired");
                    }
                    if (e instanceof MalformedJwtException) {
                        return sendErrorResponse(response, "Invalid JWT Token");
                    } else return sendErrorResponse(response, e.getLocalizedMessage());
                }

                String username = jwtUtil.getUsernameFromToken(token);
                User user = userRepository.findByUsername(username).orElse(null);

                assert user != null;
                setCustomizedHeaders(customizedRequest,
                        header("X-USER-ID", String.valueOf(user.getId())),
                        header("X-USER-USERNAME", user.getUsername()),
                        header("X-USER-EMAIL", user.getEmail()),
                        header("X-USER-PHONE", user.getPhone()),
                        header("X-USER-FIRST_NAME", user.getFirstName()),
                        header("X-USER-LAST_NAME", user.getLastName()),
                        header("X-USER-FULL_NAME", user.getFirstName() + " " + user.getLastName()),
                        header("X-USER-DATE_OF_BIRTH", user.getDateOfBirth()),
                        header("X-USER-ROLL", user.getRole().getRole())
                );

                switch (user.getRole()) {
                    case ADMIN -> setCustomizedHeaders(customizedRequest, header("X-ADMIN-SECRET", adminSecretKey));
                    case EMPLOYEE -> setCustomizedHeaders(customizedRequest, header("X-EMPLOYEE-SECRET", employeeSecretKey));
                }
            }

            return chain.filter(exchange.mutate().request(customizedRequest.build()).build());
        });
    }

    private String[] header(String key, String value) {
        return new String[] {key, value};
    }

    private void setCustomizedHeaders(ServerHttpRequest.Builder builder, String[]... headers) {
        for (String[] header : headers) {
            builder.header(header[0], header[1]);
        }
    }

    private Mono<Void> sendErrorResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorBody = String.format
                ("""
                                {
                                   "status": %b,
                                   "message": "%s"
                                }
                                """,
                        false,
                        message
                );

        DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
    }
}
