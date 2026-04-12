package com.group8.service;

import com.group8.dto.UserInfoUpdateRequest;
import com.group8.dto.UserQueryRequest;
import com.group8.entity.Result;
import com.group8.entity.User;

public interface UserService {
    User getUserByUsername(String username);
    User getUserById(Integer userId);

    /**
     *
     * 上面两个是认证管理用到的接口
     * 下面是用户管理用到的接口
     *
     */


    // 获取当前登录用户信息
    Result getCurrentUserInfo(Long userId);

    // 更新当前登录用户信息
    Result updateCurrentUserInfo(UserInfoUpdateRequest request, Long userId);

    // 管理员获取用户列表
    Result getUserList(UserQueryRequest queryRequest, Long adminId);

    // 管理员获取用户详情
    Result getUserDetail(Long userId, Long adminId);

    // 管理员删除供应商
    Result deleteSupplier(Long supplierId, Long adminId);

    // 管理员删除采购商
    Result deleteBuyer(Long buyerId, Long adminId);
}
