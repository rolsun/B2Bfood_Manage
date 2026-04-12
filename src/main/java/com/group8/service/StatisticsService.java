package com.group8.service;

import com.group8.entity.Result;

public interface StatisticsService {
    
    /**
     * 按角色统计用户数量
     */
    Result getUserCountByRole();
    
    /**
     * 按类别统计商品数量
     */
    Result getProductCountByCategory();
}
