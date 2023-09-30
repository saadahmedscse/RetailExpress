package com.saadahmedev.apigateway.security;

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

    @Override
    public GatewayFilter apply(JwtAuthenticationFilter.Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return sendErrorResponse(response, "Authorization bearer token is required");
                }

                String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (token == null)  return sendErrorResponse(response, "Authorization bearer token is required");

                if (!token.startsWith("Bearer ") || !(token.length() > 15)) return sendErrorResponse(response, "Token does not match with JWT Token");
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
