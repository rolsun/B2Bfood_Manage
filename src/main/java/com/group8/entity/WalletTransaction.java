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
}
