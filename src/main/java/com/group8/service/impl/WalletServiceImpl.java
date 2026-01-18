package com.group8.service.impl;

import com.group8.dto.*;
import com.group8.entity.*;
import com.group8.mapper.WalletMapper;
import com.group8.mapper.WalletTransactionMapper;
import com.group8.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    
    @Autowired
    private WalletMapper walletMapper;
    
    @Autowired
    private WalletTransactionMapper transactionMapper;
    
    @Override
    public Result getWalletInfo(Long userId) {
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            // 如果用户没有钱包，创建一个
            createWalletIfNotExists(userId);
            wallet = walletMapper.selectByUserId(userId);
        }
        
        return Result.success(wallet);
    }
    
    @Override
    @Transactional
    public Result recharge(WalletRechargeRequest request, Long userId) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }
        
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            createWalletIfNotExists(userId);
            wallet = walletMapper.selectByUserId(userId);
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.add(request.getAmount());
        
        // 执行充值
        int result = walletMapper.recharge(userId, request.getAmount());
        if (result <= 0) {
            return Result.error("充值失败");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(1); // 充值
        transaction.setAmount(request.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "钱包充值");
        transaction.setStatus(1); // 成功
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        log.info("用户 {} 充值成功，金额：{}，余额：{}", userId, request.getAmount(), afterBalance);
        return Result.success("充值成功");
    }
    
    @Override
    @Transactional
    public Result pay(WalletPayRequest request, Long userId) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("支付金额必须大于0");
        }
        
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            return Result.error("钱包不存在");
        }
        
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            return Result.error("余额不足");
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.subtract(request.getAmount());
        
        // 执行支付
        int result = walletMapper.pay(userId, request.getAmount());
        if (result <= 0) {
            return Result.error("支付失败，可能余额不足");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(2); // 用户支付
        transaction.setAmount(request.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setOrderId(request.getOrderId());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "钱包支付");
        transaction.setStatus(1); // 成功
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        log.info("用户 {} 支付成功，金额：{}，余额：{}", userId, request.getAmount(), afterBalance);
        return Result.success("支付成功");
    }
    
    @Override
    public Result getTransactionRecords(WalletTransactionQueryRequest queryRequest, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("page", (queryRequest.getPage() - 1) * queryRequest.getLimit());
        params.put("limit", queryRequest.getLimit());
        params.put("transactionType", queryRequest.getTransactionType());
        params.put("startTime", queryRequest.getStartTime());
        params.put("endTime", queryRequest.getEndTime());
        
        List<WalletTransaction> transactions = transactionMapper.selectTransactions(params);
        int total = transactionMapper.countTransactions(params);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", transactions);
        
        return Result.success(result);
    }
    
    @Override
    @Transactional
    public Result withdraw(WalletWithdrawRequest request, Long userId) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("提现金额必须大于0");
        }
        
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            return Result.error("钱包不存在");
        }
        
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            return Result.error("余额不足");
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.subtract(request.getAmount());
        
        // 执行提现
        int result = walletMapper.withdraw(userId, request.getAmount());
        if (result <= 0) {
            return Result.error("提现失败，可能余额不足");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(4); // 提现
        transaction.setAmount(request.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "钱包提现");
        transaction.setStatus(1); // 成功
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        log.info("用户 {} 提现成功，金额：{}，余额：{}", userId, request.getAmount(), afterBalance);
        return Result.success("提现成功");
    }
    
    @Override
    public void createWalletIfNotExists(Long userId) {
        Wallet existingWallet = walletMapper.selectByUserId(userId);
        if (existingWallet == null) {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setTotalRecharge(BigDecimal.ZERO);
            wallet.setTotalSpent(BigDecimal.ZERO);
            wallet.setCreateTime(LocalDateTime.now());
            wallet.setUpdateTime(LocalDateTime.now());
            
            walletMapper.insert(wallet);
            log.info("为用户 {} 创建初始钱包", userId);
        }
    }
    
    @Override
    @Transactional
    public Result income(BigDecimal amount, Long userId, Long orderId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("收入金额必须大于0");
        }
        
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            createWalletIfNotExists(userId);
            wallet = walletMapper.selectByUserId(userId);
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.add(amount);
        
        // 增加余额
        int result = walletMapper.increaseBalance(userId, amount);
        if (result <= 0) {
            return Result.error("收入操作失败");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(2); // 用户支付（供应商收到支付）
        transaction.setAmount(amount);
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setOrderId(orderId);
        transaction.setDescription(description != null ? description : "订单收入");
        transaction.setStatus(1); // 成功
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        log.info("用户 {} 收入成功，金额：{}，余额：{}", userId, amount, afterBalance);
        return Result.success("收入成功");
    }
    
    @Override
    @Transactional
    public Result refund(BigDecimal amount, Long userId, Long orderId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("退款金额必须大于0");
        }
        
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            createWalletIfNotExists(userId);
            wallet = walletMapper.selectByUserId(userId);
        }
        
        BigDecimal beforeBalance = wallet.getBalance();
        BigDecimal afterBalance = beforeBalance.add(amount);
        
        // 退款实际上是增加余额
        int result = walletMapper.increaseBalance(userId, amount);
        if (result <= 0) {
            return Result.error("退款操作失败");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(3); // 售后退款
        transaction.setAmount(amount);
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setOrderId(orderId);
        transaction.setDescription(description != null ? description : "售后退款");
        transaction.setStatus(1); // 成功
        transaction.setCreateTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        log.info("用户 {} 退款成功，金额：{}，余额：{}", userId, amount, afterBalance);
        return Result.success("退款成功");
    }
}