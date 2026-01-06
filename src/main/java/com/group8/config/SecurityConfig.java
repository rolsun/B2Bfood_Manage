package com.group8.config;

import com.group8.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/categories/create").hasRole("ADMIN")  // 仅管理员可创建分类
                        .requestMatchers(HttpMethod.PUT, "/categories").hasRole("ADMIN")//仅管理员可修改分类
                        .requestMatchers(HttpMethod.DELETE, "/categories").hasRole("ADMIN")//仅管理员可删除分类


                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/products/add").hasRole("SUPPLIER")  // 供应商创建商品
                        .requestMatchers("/products/supplier").hasRole("SUPPLIER")//供应商查看商品
                        .requestMatchers(HttpMethod.PUT,"/products").hasRole("SUPPLIER")//供应商修改商品
                        .requestMatchers(HttpMethod.DELETE,"/products").hasRole("SUPPLIER")//供应商删除商品
                        .requestMatchers(HttpMethod.GET, "/products").hasRole("PURCHASER")  // 采购商获取商品列表

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态会话
                )
                .csrf(csrf -> csrf.disable()) // 禁用CSRF（如果使用JWT）
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
