package com.group8.dto;

import com.group8.entity.OrderItem;
import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponse {
    private Long orderId;
    private String orderSn;
    private Double totalAmount;
    private Integer orderStatus;
    private Integer paymentMethod;
    private Long buyerId;
    private String buyerName;
    private Long supplierId;
    private String supplierName;
    private DeliveryAddress deliveryAddress;
    private String buyerNote;
    private String cancelReason;
    private ShippingInfo shippingInfo;
    private String createTime;
    private String updateTime;
    private List<OrderItem> items;
}
