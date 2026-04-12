package com.group8.dto;

import lombok.Data;

@Data
public class OrderUpdateRequest {
    private Long deliveryAddressId;
    private String buyerNote;
}
