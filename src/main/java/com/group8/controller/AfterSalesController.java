package com.group8.controller;

import com.group8.entity.AfterSales;
import com.group8.entity.Result;
import com.group8.entity.CustomUserDetails;
import com.group8.service.AfterSalesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/after-sales")
public class AfterSalesController {

    @Autowired
    private AfterSalesService afterSalesService;

    /**
     * 获取当前认证的用户名
     */
    private String getCurrentUsername(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        throw new RuntimeException("无法获取当前用户名");
    }

    /**
     * 获取当前认证的用户id
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }

    /**
     * 提交售后申请
     * 权限: 仅采购商可调用,需携带有效Token
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result submitAfterSales(@RequestBody AfterSales afterSales, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        return afterSalesService.submitAfterSales(afterSales, username);
    }

    /**
     * 处理售后申请（供应商操作）
     * 权限: 仅供应商可调用,需携带有效Token
     */
    @PutMapping("/process/{id}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result processAfterSales(@PathVariable Long id, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        return afterSalesService.processAfterSales(id, username);
    }

    /**
     * 取消售后申请（采购商操作）
     * 权限: 仅采购商可调用,且只能取消待处理状态的售后申请
     */
    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result cancelAfterSales(@PathVariable Long id, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        return afterSalesService.cancelAfterSales(id, username);
    }

    /**
     * 获取售后申请详情
     * 权限: 采购商或供应商可调用,需携带有效Token
     */
    @GetMapping("/{id}")
    public Result getAfterSalesDetail(@PathVariable Long id, Authentication authentication) {
        String username = getCurrentUsername(authentication);
        return afterSalesService.getAfterSalesDetail(id, username);
    }

    /**
     * 获取供应商待处理的售后申请列表
     * 权限: 仅供应商可调用,需携带有效Token
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result getPendingAfterSalesForSupplier(Authentication authentication) {
        Long supplierId = getCurrentUserId(authentication);
        return afterSalesService.getPendingAfterSalesForSupplier(supplierId);
    }

    /**
     * 获取采购商提交的售后申请列表
     * 权限: 仅采购商可调用,需携带有效Token
     */
    @GetMapping("/my-list")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result getAfterSalesForBuyer(Authentication authentication) {
        Long buyerId = getCurrentUserId(authentication);
        log.info("获取采购商售后申请列表,用户ID: {}", buyerId);
        return afterSalesService.getAfterSalesForBuyer(buyerId);
    }
}
