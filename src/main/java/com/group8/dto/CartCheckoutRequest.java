package com.group8.dto;

import lombok.Data;

@Data
public class CartCheckoutRequest {
    private Long deliveryAddressId;
    private Integer paymentMethod;
    private String buyerNote;
}
