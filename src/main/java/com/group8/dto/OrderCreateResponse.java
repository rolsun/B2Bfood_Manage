package com.group8.dto;

import lombok.Data;

@Data
public class OrderCreateResponse {
    private Long orderId;
    private String orderSn;
    private Double totalAmount;
    private Integer orderStatus;
}
