package com.atguigu.spzx.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> authHeader = request.getHeaders().get("Authorization");

        String path = request.getURI().getPath();
        //判断路径 /api/**/auth/**，登录校验
        if(antPathMatcher.match("/api/**/auth/**",path)){
            //登录校验
            UserInfo userInfo = this.getUserInfo(request);
            if(userInfo == null){
                ServerHttpResponse response = exchange.getResponse();
                return out(response,ResultCodeEnum.LOGIN_AUTH);
            }
        }

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

    private UserInfo getUserInfo(ServerHttpRequest request) {
        String token = "";
        List<String> tokenList = request.getHeaders().get("token");
        if(tokenList != null && !tokenList.isEmpty()){
            token = tokenList.get(0);
        }
        if(token != null && !token.isEmpty()){
            String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
            if(userJson == null || userJson.isEmpty()){
                return null;
            }else {
                return JSONObject.parseObject(userJson, UserInfo.class);
            }
        }
        return null;
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

    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        Result result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中中文会乱码
        response.getHeaders().add("Content-Type", "application/json; charset=utf-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // 高优先级，确保在网关其他过滤器之前执行
    }
}
