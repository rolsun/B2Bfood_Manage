package com.group8.dto;

import com.group8.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateResponse {
    private Integer totalOrderCount;
    private List<OrderInfo> orders;

    @Data
    public static class OrderInfo {
        private Long orderId;
        private String orderSn;
        private Double totalAmount;
        private Integer orderStatus;
        private Long supplierId;
        private LocalDateTime createTime;

        public OrderInfo(Order order) {
            this.orderId = order.getId();
            this.orderSn = order.getOrderSn();
            this.totalAmount = order.getTotalAmount().doubleValue();
            this.orderStatus = order.getOrderStatus();
            this.supplierId = order.getSupplierId();
            this.createTime = order.getCreateTime();
        }

        // 用于手动创建
        public OrderInfo(Long orderId, String orderSn, Double totalAmount,
                         Integer orderStatus, Long supplierId, LocalDateTime createTime) {
            this.orderId = orderId;
            this.orderSn = orderSn;
            this.totalAmount = totalAmount;
            this.orderStatus = orderStatus;
            this.supplierId = supplierId;
            this.createTime = createTime;
        }
    }

    // 为旧的单订单响应添加兼容性方法
    public void setOrderId(Long orderId) {
        // 兼容性方法，如果需要支持旧的单订单响应
    }

    public void setOrderSn(String orderSn) {
        // 兼容性方法
    }

    public void setTotalAmount(Double totalAmount) {
        // 兼容性方法
    }

    public void setOrderStatus(Integer orderStatus) {
        // 兼容性方法
    }
}
