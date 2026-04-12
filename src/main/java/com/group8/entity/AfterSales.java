package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AfterSales {
    private Long id;
    private String applicationContent; // 申请内容
    private String complainedSupplier; // 投诉的供应商
    private String complainedOrder; // 投诉的订单
    private Integer status=1; // 状态(1为待处理,2为已处理)
    private LocalDateTime createdTime; // 创建时间
    private Integer afterSalesType; // 售后类型(1-退货退款, 2-仅退款, 3-换货)

    // 构造函数
    public AfterSales() {}

    public AfterSales(String applicationContent, String complainedSupplier, 
                      String complainedOrder, Integer afterSalesType) {
        this.applicationContent = applicationContent;
        this.complainedSupplier = complainedSupplier;
        this.complainedOrder = complainedOrder;
        this.afterSalesType = afterSalesType;
        this.status = 1; // 默认为待处理状态
    }
}
