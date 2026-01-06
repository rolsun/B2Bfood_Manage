package com.group8.controller;

import com.group8.entity.Result;
import com.group8.entity.User;
import com.group8.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     * @param params 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        User user = userService.login(username, password);
        return Result.success("登录成功", user);
    }
    
    /**
     * 获取用户列表
     * @param page 页码
     * @param limit 每页条目数
     * @param username 用户名搜索
     * @param status 用户状态
     * @return 用户列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("status", status);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        // 获取用户列表
        List<User> users = userService.list(params);
        // 获取用户总数
        int total = userService.count(params);
        
        // 构建响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", users);
        
        return Result.success(result);
    }
    
    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param params 更新参数
     * @return 影响行数
     */
    @PutMapping("/{id}/status")
    public Result<Integer> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        int result = userService.updateStatus(id, status);
        return Result.success("更新状态成功", result);
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Integer id) {
        int result = userService.delete(id);
        return Result.success("删除用户成功", result);
    }
}
