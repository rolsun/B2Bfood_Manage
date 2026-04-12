package com.group8.controller;

import com.group8.entity.LoginRequest;
import com.group8.entity.RegisterRequest;
import com.group8.entity.Result;
import com.group8.entity.User;
import com.group8.service.AuthService;
import com.group8.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;


    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginRequest loginRequest) {

        log.info("用户登录,用户名: {}", loginRequest.getUsername());

            Map<String, Object> loginResult = authService.loginWithUserInfo(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            loginResult.put("message", "登录成功");

            return Result.success(loginResult);

    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Validated RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUserName(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setUserType(registerRequest.getUserType());
            
            authService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success("登出成功");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}




