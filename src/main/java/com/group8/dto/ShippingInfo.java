package com.group8.dto;

import lombok.Data;

@Data
public class ShippingInfo {
    private String shippingCompany;
    private String trackingNumber;
    private String estimatedDeliveryTime;
    private String actualDeliveryTime;
}
