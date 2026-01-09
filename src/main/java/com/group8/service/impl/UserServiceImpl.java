package com.group8.service.impl;

import com.group8.dto.UserInfoUpdateRequest;
import com.group8.dto.UserQueryRequest;
import com.group8.entity.Result;
import com.group8.entity.User;
import com.group8.mapper.UserMapper;
import com.group8.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.findById(userId);
    }

    @Override
    public Result getCurrentUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 移除敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result updateCurrentUserInfo(UserInfoUpdateRequest request, Long userId) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        int result = userMapper.updateUserInfo(userId, request);
        if (result > 0) {
            return Result.success("用户信息更新成功");
        } else {
            return Result.error("用户信息更新失败");
        }
    }

    @Override
    public Result getUserList(UserQueryRequest queryRequest, Long adminId) {
        // 验证管理员权限
        User admin = userMapper.selectById(adminId);
        if (admin == null || admin.getUserType() != 3) { // 3-管理员
            return Result.error("无权访问");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("page", (queryRequest.getPage() - 1) * queryRequest.getLimit());
        params.put("limit", queryRequest.getLimit());
        params.put("keyword", queryRequest.getKeyword());
        params.put("userType", queryRequest.getUserType());
        params.put("startTime", queryRequest.getStartTime());
        params.put("endTime", queryRequest.getEndTime());

        List<User> users = userMapper.selectUsers(params);
        int total = userMapper.countUsers(params);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", users);

        return Result.success(result);
    }
    @Override
    public Result getUserDetail(Long userId, Long adminId) {
        // 验证管理员权限
        User admin = userMapper.selectById(adminId);
        if (admin == null || admin.getUserType() != 3) {
            return Result.error("无权访问");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 移除敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result deleteSupplier(Long supplierId, Long adminId) {
        // 验证管理员权限
        User admin = userMapper.selectById(adminId);
        if (admin == null || admin.getUserType() != 3) {
            return Result.error("无权删除用户");
        }

        // 验证要删除的用户是供应商
        User supplier = userMapper.selectById(supplierId);
        if (supplier == null || supplier.getUserType() != 2) {
            return Result.error("用户不存在或不是供应商");
        }

        // 删除供应商的所有商品
        userMapper.deleteSupplierProducts(supplierId);

        // 删除供应商
        int result = userMapper.deleteById(supplierId);
        if (result > 0) {
            log.info("管理员 {} 删除供应商 {} 成功", adminId, supplierId);
            return Result.success("供应商删除成功");
        } else {
            return Result.error("供应商删除失败");
        }
    }

    @Override
    @Transactional
    public Result deleteBuyer(Long buyerId, Long adminId) {
        // 验证管理员权限
        User admin = userMapper.selectById(adminId);
        if (admin == null || admin.getUserType() != 3) {
            return Result.error("无权删除用户");
        }

        // 验证要删除的用户是采购商
        User buyer = userMapper.selectById(buyerId);
        if (buyer == null || buyer.getUserType() != 1) {
            return Result.error("用户不存在或不是采购商");
        }

        // 删除采购商的所有订单
        userMapper.deleteBuyerOrders(buyerId);

        // 删除采购商的购物车
        userMapper.deleteBuyerCart(buyerId);

        // 删除采购商
        int result = userMapper.deleteById(buyerId);
        if (result > 0) {
            log.info("管理员 {} 删除采购商 {} 成功", adminId, buyerId);
            return Result.success("采购商删除成功");
        } else {
            return Result.error("采购商删除失败");
        }
    }

}
