package com.group8.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletPayRequest {
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;
    private Long orderId;
    private String description;
}
