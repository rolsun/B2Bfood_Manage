package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 售后申请实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSales {
    /**
     * 售后申请ID
     */
    private Integer id;
    
    /**
     * 订单ID
     */
    private Integer orderId;
    
    /**
     * 采购商ID
     */
    private Integer buyerId;
    
    /**
     * 供应商ID
     */
    private Integer sellerId;
    
    /**
     * 售后类型: 1-退货退款, 2-仅退款, 3-换货
     */
    private Integer afterSalesType;
    
    /**
     * 售后状态: 1-待审核,2-审核通过,3-审核拒绝,4-待退货,5-待收货,6-已完成,7-已关闭
     */
    private Integer status;
    
    /**
     * 申请原因
     */
    private String reason;
    
    /**
     * 申请退款金额
     */
    private Double refundAmount;
    
    /**
     * 审核意见
     */
    private String reviewComment;
}
