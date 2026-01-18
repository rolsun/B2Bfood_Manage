package com.group8.util;

import com.group8.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String SECRET_KEY = "your-secret-key"; // 从配置文件读取

    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUserName());
        // 根据userType映射角色
        String role = mapUserTypeToRole(user.getUserType());
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();//compact()的作用是将JWT构建成字符串格式返回
    }

    //从Jwt令牌中提取角色信息的 方法
    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("role");
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUserName()));
    }

    // 根据userType映射为Spring Security角色
    private String mapUserTypeToRole(Integer userType) {
        switch (userType) {
            case 1: return "ROLE_PURCHASER";
            case 2: return "ROLE_SUPPLIER";
            case 3: return "ROLE_ADMIN";
            default: return "ROLE_USER";
        }
    }

    //获取过期时间
    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

}
