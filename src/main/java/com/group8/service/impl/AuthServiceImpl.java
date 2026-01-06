package com.group8.service.impl;

import com.group8.entity.User;
import com.group8.mapper.UserMapper;
import com.group8.service.AuthService;
import com.group8.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";


    @Override
    public String login(String username, String password) throws Exception {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(user);
        } else {
            throw new Exception("用户名或密码错误");
        }
    }

    @Override
    public void register(User user) throws Exception {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUserName());
        if (existingUser != null) {
            throw new Exception("用户名已存在");
        }

        // 验证用户类型
        if (user.getUserType() != 1 && user.getUserType() != 2 && user.getUserType() != 3) {
            throw new Exception("用户类型无效：1为采购商, 2为供应商, 3为管理员");
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 保存用户
        userMapper.insert(user);
    }

    @Override
    public void logout(String token) {
        // 获取 Token 过期时间
        Date expiration = jwtUtil.getExpirationDate(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        // 将 Token 加入黑名单
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1",
                Duration.ofMillis(Math.max(ttl, 0)));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String value = redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token);
        return "1".equals(value);
    }

}
