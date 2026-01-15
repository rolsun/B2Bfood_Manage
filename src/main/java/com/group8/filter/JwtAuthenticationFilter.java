package com.group8.filter;

import com.group8.entity.CustomUserDetails;
import com.group8.entity.User;
import com.group8.service.AuthService;
import com.group8.service.UserService;
import com.group8.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("无法解析JWT token");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 检查 Token 是否在黑名单中
            if (authService.isTokenBlacklisted(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token 已失效，请重新登录");
                return;
            }

            User user = userService.getUserByUsername(username);

            if (user != null && jwtUtil.validateToken(jwt, user)) {
                // 创建权限列表
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                String role = mapUserTypeToRole(user.getUserType());
                authorities.add(new SimpleGrantedAuthority(role));

                // 创建 CustomUserDetails 对象
                CustomUserDetails userDetails = new CustomUserDetails(
                        user.getUserName(),
                        user.getPassword(),
                        user.getUserType(),
                        authorities
                );

                // 设置用户ID
                userDetails.setId(user.getUserId());

                // 创建认证对象
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

    private String mapUserTypeToRole(Integer userType) {
        switch (userType) {
            case 1: return "ROLE_PURCHASER";
            case 2: return "ROLE_SUPPLIER";
            case 3: return "ROLE_ADMIN";
            default: return "ROLE_USER";
        }
    }
}
