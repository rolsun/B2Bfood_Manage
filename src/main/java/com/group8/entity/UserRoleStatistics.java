package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleStatistics {
    private Integer userType; // 用户类型: 1-采购商, 2-供应商, 3-管理员
    private String roleName;  // 角色名称
    private Long count;       // 用户数量
}
