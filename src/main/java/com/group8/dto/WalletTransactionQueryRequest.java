package com.group8.dto;

import lombok.Data;

@Data
public class WalletTransactionQueryRequest {
    private Integer page = 1;
    private Integer limit = 10;
    private Integer transactionType; // 1-充值, 2-支付, 3-提现
    private String startTime;
    private String endTime;
}
