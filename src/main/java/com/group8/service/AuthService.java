package com.group8.service;

public interface AuthService {
    String login(String username, String password) throws Exception;

    void register(com.group8.entity.User user) throws Exception;

    void logout(String token);

    boolean isTokenBlacklisted(String token);
}
