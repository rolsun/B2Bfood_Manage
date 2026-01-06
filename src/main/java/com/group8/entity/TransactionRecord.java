package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 交易记录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    /**
     * 交易记录ID
     */
    private Integer id;
    
    /**
     * 所属钱包ID
     */
    private Integer walletId;
    
    /**
     * 交易类型: 1-充值, 2-提现, 3-支付
     */
    private Integer type;
    
    /**
     * 交易金额(元)
     */
    private Double amount;
    
    /**
     * 交易后余额(元)
     */
    private Double balanceAfter;
    
    /**
     * 支付方式(仅充值有此参数)
     */
    private String paymentMethod;
    
    /**
     * 银行卡号(仅提现有此参数)
     */
    private String bankCardNumber;
    
    /**
     * 收款方ID(仅支付有此参数)
     */
    private Integer payeeId;
    
    /**
     * 交易时间
     */
    private Date transactionTime;
    
    /**
     * 交易备注
     */
    private String remark;
}