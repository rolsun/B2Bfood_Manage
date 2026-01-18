package com.group8.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransaction {
    private Long id;
    private Long userId;
    private Integer transactionType;
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private Long orderId;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    
    // 添加方法来获取交易类型的描述
    public String getTransactionTypeDescription() {
        switch (transactionType) {
            case 1:
                return "充值";
            case 2:
                return "用户支付";
            case 3:
                return "售后退款";
            case 4:
                return "提现";
            default:
                return "未知类型";
        }
    }
}