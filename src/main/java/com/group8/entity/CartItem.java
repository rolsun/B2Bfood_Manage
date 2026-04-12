package com.group8.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


    private String productName;
    private Double price;
    private String image;
    private String unit;
}
