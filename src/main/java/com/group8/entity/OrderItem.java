package com.group8.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String note;
    private LocalDateTime createTime;
}
