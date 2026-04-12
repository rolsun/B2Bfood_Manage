package com.group8.dto;

import lombok.Data;

@Data
public class OrderQueryRequest {
    private Integer page = 1;
    private Integer limit = 10;
    private String orderSn;
    private Integer status;
    private String startTime;
    private String endTime;
}
