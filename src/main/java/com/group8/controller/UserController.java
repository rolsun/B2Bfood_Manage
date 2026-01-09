package com.group8.controller;

import com.group8.dto.*;
import com.group8.entity.CustomUserDetails;
import com.group8.entity.Result;
import com.group8.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前登录用户信息
     * 权限: 需携带有效Token，限管理员、采购商、供应商
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASER', 'SUPPLIER')")
    public Result getCurrentUserInfo(Authentication authentication) {
        log.info("获取当前用户信息");
        Long userId = getCurrentUserId(authentication);
        return userService.getCurrentUserInfo(userId);
    }
    
    /**
     * 更新当前登录用户信息
     * 权限: 需携带有效Token，限管理员、采购商、供应商
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASER', 'SUPPLIER')")
    public Result updateCurrentUserInfo(@Valid @RequestBody UserInfoUpdateRequest request, 
                                      Authentication authentication) {
        log.info("更新当前用户信息，请求：{}", request);
        Long userId = getCurrentUserId(authentication);
        return userService.updateCurrentUserInfo(request, userId);
    }
    
    /**
     * 管理员获取所有已注册采购商、供应商用户信息列表
     * 权限: 仅管理员可调用
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        
        log.info("管理员获取用户列表，查询参数：page={}, limit={}, keyword={}, userType={}", 
                page, limit, keyword, userType);
        
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setPage(page);
        queryRequest.setLimit(limit);
        queryRequest.setKeyword(keyword);
        queryRequest.setUserType(userType);
        queryRequest.setStartTime(startTime);
        queryRequest.setEndTime(endTime);
        
        Long adminId = getCurrentUserId(authentication);
        return userService.getUserList(queryRequest, adminId);
    }
    
    /**
     * 管理员获取所选用户详情
     * 权限: 仅管理员可调用
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result getUserDetail(@PathVariable Long userId, Authentication authentication) {
        log.info("管理员获取用户详情，用户ID：{}", userId);
        Long adminId = getCurrentUserId(authentication);
        return userService.getUserDetail(userId, adminId);
    }
    
    /**
     * 管理员删除所选供应商（并且直接删除供应商下的所有商品）
     * 权限: 仅管理员可调用
     */
    @DeleteMapping("/supplier/{supplierId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteSupplier(@PathVariable Long supplierId, Authentication authentication) {
        log.info("管理员删除供应商，供应商ID：{}", supplierId);
        Long adminId = getCurrentUserId(authentication);
        return userService.deleteSupplier(supplierId, adminId);
    }
    
    /**
     * 管理员删除所选采购商（包括该采购商下的所有订单、购物车等）
     * 权限: 仅管理员可调用
     */
    @DeleteMapping("/buyer/{buyerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteBuyer(@PathVariable Long buyerId, Authentication authentication) {
        log.info("管理员删除采购商，采购商ID：{}", buyerId);
        Long adminId = getCurrentUserId(authentication);
        return userService.deleteBuyer(buyerId, adminId);
    }
    
    // 私有方法
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
