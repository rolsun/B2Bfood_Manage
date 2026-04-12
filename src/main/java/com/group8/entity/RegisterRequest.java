package com.group8.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//注册请求实体
public class RegisterRequest {
    /**
     * 三个都不能为空,用户名长度在3-20之间,密码长度在6-20之间
     */
    @NotBlank(message = "用户名不能为空") // Import javax.validation.constraints.NotBlank
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    @NotNull(message = "用户类型不能为空")
    private Integer userType;


    public User toUser() {
        User user = new User();
        user.setUserName(this.username);
        user.setPassword(this.password);
        user.setUserType(this.userType);
        return user;
    }


}
