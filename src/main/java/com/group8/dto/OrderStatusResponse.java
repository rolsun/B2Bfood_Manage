package com.group8.dto;

import lombok.Data;

@Data
public class OrderStatusResponse {
    private Long orderId;
    private String orderSn;
    private Integer orderStatus;
    private String updateTime;
}
