package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//注册请求实体
public class RegisterRequest {
    private String username;
    private String password;
    private Integer userType;


    public User toUser() {
        User user = new User();
        user.setUserName(this.username);
        user.setPassword(this.password);
        user.setUserType(this.userType);
        return user;
    }


}
