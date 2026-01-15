package com.group8.controller;

import com.group8.dto.*;
import com.group8.entity.CustomUserDetails;
import com.group8.entity.Result;
import com.group8.service.WalletService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 获取当前用户钱包信息
     * 权限: 需携带有效Token，限采购商和供应商
     */
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('PURCHASER', 'SUPPLIER')")
    public Result getWalletInfo(Authentication authentication) {
        log.info("获取钱包信息");
        Long userId = getCurrentUserId(authentication);
        return walletService.getWalletInfo(userId);
    }
    
    /**
     * 钱包充值
     * 权限: 仅限采购商
     */
    @PostMapping("/recharge")
    @PreAuthorize("hasAnyRole('PURCHASER', 'SUPPLIER')")
    public Result recharge(@Valid @RequestBody WalletRechargeRequest request, Authentication authentication) {
        log.info("钱包充值，请求：{}", request);
        Long userId = getCurrentUserId(authentication);
        return walletService.recharge(request, userId);
    }
    
    /**
     * 钱包支付
     * 权限: 仅限采购商
     */
    @PostMapping("/pay")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result pay(@Valid @RequestBody WalletPayRequest request, Authentication authentication) {
        log.info("钱包支付，请求：{}", request);
        Long userId = getCurrentUserId(authentication);
        return walletService.pay(request, userId);
    }
    
    /**
     * 获取钱包交易记录
     * 权限: 需携带有效Token，限采购商和供应商
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('PURCHASER', 'SUPPLIER')")
    public Result getTransactionRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer transactionType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        
        log.info("获取钱包交易记录，用户ID：{}，查询参数：page={}, limit={}, type={}", 
                getCurrentUserId(authentication), page, limit, transactionType);
        
        WalletTransactionQueryRequest queryRequest = new WalletTransactionQueryRequest();
        queryRequest.setPage(page);
        queryRequest.setLimit(limit);
        queryRequest.setTransactionType(transactionType);
        queryRequest.setStartTime(startTime);
        queryRequest.setEndTime(endTime);
        
        Long userId = getCurrentUserId(authentication);
        return walletService.getTransactionRecords(queryRequest, userId);
    }
    
    /**
     * 钱包提现
     * 权限: 仅限采购商和供应商
     */
    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('PURCHASER', 'SUPPLIER')")
    public Result withdraw(@Valid @RequestBody WalletWithdrawRequest request, Authentication authentication) {
        log.info("钱包提现，请求：{}", request);
        Long userId = getCurrentUserId(authentication);
        return walletService.withdraw(request, userId);
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
