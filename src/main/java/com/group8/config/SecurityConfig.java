package com.group8.config;

import com.group8.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 配置：允许跨域并禁用 CSRF
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // 2. 无状态会话配置
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authz -> authz

                        // ================= Swagger 放行（新增） =================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 3. 核心修复：放行所有 OPTIONS 预检请求，解决前端“接口连接失败”
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                        .requestMatchers("/categories/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories").hasRole("ADMIN")

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/products/add").hasRole("SUPPLIER")
                        .requestMatchers("/products/supplier").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.PUT,"/products").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.DELETE,"/products").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.GET, "/products").hasRole("PURCHASER")

                        .requestMatchers("/orders").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers("/orders/*").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.PUT, "/orders/*").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.POST, "/orders/*/cancel").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.GET, "/orders/supplier/pending").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/orders/*/delivery").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/orders/cart/checkout").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.POST, "/orders/pay/*").hasRole("PURCHASER")

                        .requestMatchers(HttpMethod.POST, "/orders/cart/add").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.GET, "/orders/cart").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.DELETE, "/orders/cart/*").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.PUT, "/orders/cart/*").hasRole("PURCHASER")

                        .requestMatchers(HttpMethod.GET, "/wallet/info").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/wallet/recharge").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/wallet/pay").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.GET, "/wallet/transactions").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/wallet/withdraw").hasAnyRole("PURCHASER", "SUPPLIER")

                        .requestMatchers(HttpMethod.GET, "/users/profile").hasAnyRole("ADMIN", "PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.PUT, "/users/profile").hasAnyRole("ADMIN", "PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/supplier/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/buyer/*").hasRole("ADMIN")

                        .requestMatchers("/api/after-sales/submit").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.PUT, "/api/after-sales/process/*").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.DELETE, "/api/after-sales/cancel/*").hasRole("PURCHASER")
                        .requestMatchers(HttpMethod.GET, "/api/after-sales/*").hasAnyRole("PURCHASER", "SUPPLIER")
                        .requestMatchers(HttpMethod.GET, "/api/after-sales/pending").hasRole("SUPPLIER")
                        .requestMatchers(HttpMethod.GET, "/api/after-sales/my-list").hasRole("PURCHASER")

                        .requestMatchers(HttpMethod.GET, "/api/statistics/user-role-count").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/statistics/product-category-count").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/statistics/overview").authenticated()

                        .anyRequest().authenticated()
                )
                // 4. 将 JWT 过滤器放在账号密码过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
