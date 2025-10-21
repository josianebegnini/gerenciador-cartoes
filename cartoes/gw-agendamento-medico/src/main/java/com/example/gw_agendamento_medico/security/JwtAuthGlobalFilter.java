package com.example.gw_agendamento_medico.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.buffer.DataBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter {

    private final JwtTokenValidator tokenValidator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> publicPaths = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    public JwtAuthGlobalFilter(JwtTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        if (!tokenValidator.validateToken(token)) {
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        return handleValidToken(exchange, chain, token);
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private Mono<Void> handleValidToken(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        return tokenValidator.getClaims(token)
                .map(claims -> {
                    String username = claims.getSubject();
                    String authorities = claims.get("roles", String.class);

                    ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                            .header("X-Username", username != null ? username : "")
                            .header("X-Authorities", authorities != null ? authorities : "")
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedReq).build());
                })
                .orElseGet(() -> unauthorizedResponse(exchange, "Could not extract claims from token"));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", message,
                "path", exchange.getRequest().getURI().getPath()
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }
}
