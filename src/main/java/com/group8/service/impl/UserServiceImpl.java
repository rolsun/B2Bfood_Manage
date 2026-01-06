package com.group8.service.impl;

import com.group8.entity.User;
import com.group8.mapper.UserMapper;
import com.group8.service.UserService;
import com.group8.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User getById(Integer id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return userMapper.getById(id);
    }
    
    @Override
    public User getByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        return userMapper.getByUsername(username);
    }
    
    @Override
    public List<User> list(Map<String, Object> params) {
        return userMapper.list(params);
    }
    
    @Override
    public int count(Map<String, Object> params) {
        return userMapper.count(params);
    }
    
    @Override
    @Transactional
    public int updateStatus(Integer id, Integer status) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (status == null) {
            throw new BusinessException("用户状态不能为空");
        }
        if (status != 1 && status != 2) {
            throw new BusinessException("无效的用户状态");
        }
        
        // 检查用户是否存在
        User existingUser = userMapper.getById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        return userMapper.updateStatus(id, status);
    }
    
    @Override
    @Transactional
    public int delete(Integer id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        User existingUser = userMapper.getById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        return userMapper.delete(id);
    }
    
    @Override
    public User login(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        User user = userMapper.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 密码验证（实际项目中应该使用加密存储和验证）
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        
        return user;
    }
}
