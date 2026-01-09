package com.group8.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {
    private Long deliveryAddressId;
    private List<OrderItemRequest> items;
    private Integer paymentMethod; // 固定为1-在线支付
    private String buyerNote;
}
