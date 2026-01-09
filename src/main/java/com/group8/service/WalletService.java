package com.group8.service;

import com.group8.dto.*;
import com.group8.entity.Result;
import com.group8.entity.Wallet;

public interface WalletService {
    
    // 获取当前用户钱包信息
    Result getWalletInfo(Long userId);
    
    // 钱包充值
    Result recharge(WalletRechargeRequest request, Long userId);
    
    // 钱包支付
    Result pay(WalletPayRequest request, Long userId);
    
    // 获取钱包交易记录
    Result getTransactionRecords(WalletTransactionQueryRequest queryRequest, Long userId);
    
    // 钱包提现
    Result withdraw(WalletWithdrawRequest request, Long userId);
    
    // 创建用户钱包
    void createWalletIfNotExists(Long userId);
}
