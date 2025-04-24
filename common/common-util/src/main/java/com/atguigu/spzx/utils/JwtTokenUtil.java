package com.atguigu.spzx.utils;

import com.atguigu.spzx.model.entity.system.SysRoleCodeUserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */

@Component
public class JwtTokenUtil {
    // 密钥，用于签名和验证 JWT，应妥善保管。HMAC-SHA 算法
    String secretString = "Online-Selection-back-lambert-little_fish";//至少需要32位字符
    SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    // JWT 的过期时间，这里设置为 10 小时
    private final Long expiration = 1000 * 60 * 60 * 10L;

    // 根据用户详细信息生成 JWT
    public String generateToken(SysRoleCodeUserId sysRoleCodeUserId) {
        //自定义的声明
        Map<String, Object> claims = new HashMap<>();
        //鉴权所需的权限角色
        claims.put("identities", sysRoleCodeUserId.getRoleCodes());
        return createToken(claims, String.valueOf(sysRoleCodeUserId.getUserId()));
    }

    // 创建 JWT 的具体方法
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)//自定义的声明
                .setSubject(subject)//存的用户id
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    //解析jwt
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
