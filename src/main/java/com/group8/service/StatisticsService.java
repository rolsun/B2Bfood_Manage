package com.group8.service;

import com.group8.entity.Statistics;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {
    /**
     * 获取销售统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getSalesStatistics(Map<String, Object> params);
    
    /**
     * 获取销售趋势
     * @param params 查询参数
     * @return 统计结果
     */
    List<Map<String, Object>> getSalesTrend(Map<String, Object> params);
    
    /**
     * 获取用户统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getUserStatistics(Map<String, Object> params);
    
    /**
     * 获取交易统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getTransactionStatistics(Map<String, Object> params);
    
    /**
     * 获取仪表盘统计数据
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getDashboardStatistics(Map<String, Object> params);
}