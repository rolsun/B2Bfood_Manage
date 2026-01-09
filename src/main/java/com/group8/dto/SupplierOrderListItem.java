package com.group8.dto;

import lombok.Data;

@Data
public class SupplierOrderListItem {
    private Long orderId;
    private String orderSn;
    private Double totalAmount;
    private Integer orderStatus;
    private Integer paymentMethod;
    private String buyerName;
    private String buyerPhone;
    private Integer itemCount;
    private String createTime;
}
