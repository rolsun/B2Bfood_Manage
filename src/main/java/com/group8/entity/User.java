package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 用户ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 用户类型: 1-采购商, 2-供应商, 3-管理员
     */
    private Integer usertype;
    
    /**
     * 用户状态: 1-正常, 2-禁用
     */
    private Integer status;
}
