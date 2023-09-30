package com.saadahmedev.apigateway.security;

import com.saadahmedev.apigateway.repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public GatewayFilter apply(JwtAuthenticationFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return sendErrorResponse(response, "Bearer token is required");
                }

                String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (token == null)  return sendErrorResponse(response, "Bearer token is required");
                if (!token.startsWith("Bearer ") || token.length() < 25) return sendErrorResponse(response, "Token does not match with JWT Token");

                try {
                    if (!jwtUtil.isTokenValid(token)) return sendErrorResponse(response, "Token has been expired");
                } catch (Exception e) {
                    if (e instanceof ExpiredJwtException) {
                        tokenRepository.deleteById(token);
                        return sendErrorResponse(response, "Token has been expired");
                    }
                    else return sendErrorResponse(response, e.getLocalizedMessage());
                }
            }

            return chain.filter(exchange);
        });
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

    public static class Config {}
}
