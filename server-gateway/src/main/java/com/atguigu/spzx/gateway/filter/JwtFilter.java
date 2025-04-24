package com.atguigu.spzx.gateway.filter;

import com.atguigu.spzx.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authHeader = request.getHeaders().get("Authorization");

        if (authHeader != null && !authHeader.isEmpty() && authHeader.get(0).startsWith("Bearer ")) {
            String token = authHeader.get(0).substring(7);
            try {
                Claims claims = jwtTokenUtil.extractAllClaims(token);
                // 提取权限
                List<String> authorities = extractAuthorities(claims);
                String userId = claims.getSubject();

                // 将认证信息通过请求头透传给下游
                exchange = mutateRequest(exchange, userId, authorities);
            } catch (ExpiredJwtException e) {
                return unauthorizedResponse(exchange, "Token expired");
            } catch (Exception e) {
                return unauthorizedResponse(exchange, "Invalid token");
            }
        }
        return chain.filter(exchange);
    }

    private List<String> extractAuthorities(Claims claims) {
        Object identitiesObj = claims.get("identities");
        List<String> authorities = new ArrayList<>();
        if (identitiesObj instanceof List<?>) {
            for (Object role : (List<?>) identitiesObj) {
                if (role instanceof String) {
                    authorities.add((String) role);
                }
            }
        }
        return authorities;
    }

    private ServerWebExchange mutateRequest(ServerWebExchange exchange, String userId, List<String> authorities) {
        return exchange.mutate().request(
                exchange.getRequest().mutate()
                        .header("X-Authenticated-UserId", userId)
                        .header("X-Authenticated-Authorities", String.join(",", authorities))
                        .build()
        ).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(message.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -1; // 高优先级，确保在网关其他过滤器之前执行
    }
}
