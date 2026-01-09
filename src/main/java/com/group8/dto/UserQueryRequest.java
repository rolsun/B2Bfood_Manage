package com.group8.dto;

import lombok.Data;

@Data
public class UserQueryRequest {
    private Integer page = 1;
    private Integer limit = 10;
    private String keyword;
    private Integer userType; // 1-采购商, 2-供应商, 3-管理员
    private String startTime;
    private String endTime;
}
