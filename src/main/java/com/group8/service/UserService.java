package com.group8.service;

import com.group8.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Integer id);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
    
    /**
     * 获取用户列表
     * @param params 查询参数
     * @return 用户列表
     */
    List<User> list(Map<String, Object> params);
    
    /**
     * 获取用户总数
     * @param params 查询参数
     * @return 用户总数
     */
    int count(Map<String, Object> params);
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 用户状态
     * @return 影响行数
     */
    int updateStatus(Integer id, Integer status);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int delete(Integer id);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    User login(String username, String password);
}
