package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer productId;
    private String name;
    private Integer categoryId;
    private String unit;
    private BigDecimal price;
    private String description;
    private Integer stock;
    //支持一张图
    private String image;
    private Long supplierId;
}
