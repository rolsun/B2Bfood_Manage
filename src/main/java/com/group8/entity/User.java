package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String userName;
    private String password;
    private Integer userType;
    private String phone;      // 新增
    private String email;      // 新增
    private String address;    // 新增
    private Integer status;    // 新增

    // 判断是否为管理员
    public boolean isAdmin() {
        return Integer.valueOf(3).equals(this.userType);
    }

    // 判断是否为采购商
    public boolean isPurchaser() {
        return Integer.valueOf(1).equals(this.userType);
    }

    // 判断是否为供应商
    public boolean isSupplier() {
        return Integer.valueOf(2).equals(this.userType);
    }
}
