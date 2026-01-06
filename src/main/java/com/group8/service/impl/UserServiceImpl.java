package com.group8.service.impl;

import com.group8.entity.User;
import com.group8.mapper.UserMapper;
import com.group8.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
