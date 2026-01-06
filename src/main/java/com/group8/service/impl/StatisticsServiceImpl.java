package com.group8.service.impl;

import com.group8.mapper.StatisticsMapper;
import com.group8.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private StatisticsMapper statisticsMapper;
    
    @Override
    public Map<String, Object> getSalesStatistics(Map<String, Object> params) {
        Map<String, Object> result = statisticsMapper.getSalesStatistics(params);
        return result != null ? result : new HashMap<>();
    }
    
    @Override
    public List<Map<String, Object>> getSalesTrend(Map<String, Object> params) {
        return statisticsMapper.getSalesTrend(params);
    }
    
    @Override
    public Map<String, Object> getUserStatistics(Map<String, Object> params) {
        Map<String, Object> result = statisticsMapper.getUserStatistics(params);
        return result != null ? result : new HashMap<>();
    }
    
    @Override
    public Map<String, Object> getTransactionStatistics(Map<String, Object> params) {
        Map<String, Object> result = statisticsMapper.getTransactionStatistics(params);
        return result != null ? result : new HashMap<>();
    }
    
    @Override
    public Map<String, Object> getDashboardStatistics(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取销售统计
        Map<String, Object> salesStats = getSalesStatistics(params);
        result.put("salesStatistics", salesStats);
        
        // 获取销售趋势
        List<Map<String, Object>> salesTrend = getSalesTrend(params);
        result.put("salesTrend", salesTrend);
        
        // 获取用户统计
        Map<String, Object> userStats = getUserStatistics(params);
        result.put("userStatistics", userStats);
        
        // 获取交易统计
        Map<String, Object> transactionStats = getTransactionStatistics(params);
        result.put("transactionStatistics", transactionStats);
        
        return result;
    }
}