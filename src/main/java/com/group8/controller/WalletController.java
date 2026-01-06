package com.group8.controller;

import com.group8.entity.Result;
import com.group8.entity.Wallet;
import com.group8.entity.TransactionRecord;
import com.group8.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 获取当前用户钱包信息
     * @return 钱包信息
     */
    @GetMapping("/me")
    public Result<Wallet> getMyWallet() {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        Wallet wallet = walletService.getByUserId(userId);
        return Result.success(wallet);
    }
    
    /**
     * 钱包充值
     * @param params 充值参数
     * @return 交易记录
     */
    @PostMapping("/recharge")
    public Result<TransactionRecord> recharge(@RequestBody Map<String, Object> params) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        Double amount = (Double) params.get("amount");
        String paymentMethod = (String) params.get("paymentMethod");
        String remark = (String) params.get("remark");
        
        TransactionRecord record = walletService.recharge(userId, amount, paymentMethod, remark);
        return Result.success("充值成功", record);
    }
    
    /**
     * 钱包支付
     * @param params 支付参数
     * @return 交易记录
     */
    @PostMapping("/pay")
    public Result<TransactionRecord> pay(@RequestBody Map<String, Object> params) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        Double amount = (Double) params.get("amount");
        Integer payeeId = (Integer) params.get("payeeId");
        String remark = (String) params.get("remark");
        
        TransactionRecord record = walletService.pay(userId, amount, payeeId, remark);
        return Result.success("支付成功", record);
    }
    
    /**
     * 获取钱包交易记录
     * @param page 页码
     * @param limit 每页条目数
     * @param type 交易类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易记录列表
     */
    @GetMapping("/transactions")
    public Result<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        // 获取交易记录列表
        var records = walletService.getTransactionRecords(userId, params);
        // 获取交易记录总数
        int total = walletService.getTransactionRecordsCount(userId, params);
        
        // 构建响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", records);
        
        return Result.success(result);
    }
}