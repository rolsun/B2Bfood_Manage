package com.group8.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderSn;
    private Long buyerId;
    private Long supplierId;
    private BigDecimal totalAmount;
    private Integer orderStatus;
    private Integer paymentMethod;
    private Long deliveryAddressId;
    private String buyerNote;
    private String cancelReason;
    private String shippingCompany;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联的订单项
    private List<OrderItem> items;
}
