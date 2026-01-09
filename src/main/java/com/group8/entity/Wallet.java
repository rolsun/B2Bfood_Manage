package com.group8.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Wallet {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalRecharge;
    private BigDecimal totalSpent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
