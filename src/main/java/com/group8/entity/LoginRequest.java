package com.group8.entity;
// 登录请求实体
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
// 登录请求实体
public class LoginRequest {

    private String username;
    private String password;

}
