package com.group8.service;

import com.group8.entity.User;

public interface UserService {
    User getUserByUsername(String username);
    User getUserById(Integer userId);
}
