package com.group8.dto;

import lombok.Data;

@Data
public class ShippingInfoRequest {
    private String shippingCompany;
    private String trackingNumber;
    private String estimatedDeliveryTime;
}
