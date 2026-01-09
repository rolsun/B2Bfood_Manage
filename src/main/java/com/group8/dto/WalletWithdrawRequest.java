package com.group8.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletWithdrawRequest {
    @NotNull(message = "提现金额不能为空")
    private BigDecimal amount;
    private String description;
}
