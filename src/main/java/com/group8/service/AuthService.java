package com.group8.service;

import java.util.Map;

public interface AuthService {

    String login(String username, String password) throws Exception;

    // 在 AuthService 接口中添加返回用户信息的方法
    Map<String, Object> loginWithUserInfo(String username, String password);



    void register(com.group8.entity.User user) throws Exception;

    void logout(String token);

    boolean isTokenBlacklisted(String token);
}
