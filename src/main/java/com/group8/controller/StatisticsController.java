package com.group8.controller;

import com.group8.entity.Result;
import com.group8.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取仪表盘统计数据
     * @param params 查询参数
     * @return 统计结果
     */
    @PostMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStatistics(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = statisticsService.getDashboardStatistics(params != null ? params : new java.util.HashMap<>());
        return Result.success("获取仪表盘统计数据成功", result);
    }
    
    /**
     * 获取销售统计
     * @param params 查询参数
     * @return 统计结果
     */
    @PostMapping("/sales")
    public Result<Map<String, Object>> getSalesStatistics(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = statisticsService.getSalesStatistics(params != null ? params : new java.util.HashMap<>());
        return Result.success("获取销售统计成功", result);
    }
    
    /**
     * 获取销售趋势
     * @param params 查询参数
     * @return 统计结果
     */
    @PostMapping("/sales/trend")
    public Result<List<Map<String, Object>>> getSalesTrend(@RequestBody(required = false) Map<String, Object> params) {
        List<Map<String, Object>> result = statisticsService.getSalesTrend(params != null ? params : new java.util.HashMap<>());
        return Result.success("获取销售趋势成功", result);
    }
    
    /**
     * 获取用户统计
     * @param params 查询参数
     * @return 统计结果
     */
    @PostMapping("/users")
    public Result<Map<String, Object>> getUserStatistics(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = statisticsService.getUserStatistics(params != null ? params : new java.util.HashMap<>());
        return Result.success("获取用户统计成功", result);
    }
    
    /**
     * 获取交易统计
     * @param params 查询参数
     * @return 统计结果
     */
    @PostMapping("/transactions")
    public Result<Map<String, Object>> getTransactionStatistics(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = statisticsService.getTransactionStatistics(params != null ? params : new java.util.HashMap<>());
        return Result.success("获取交易统计成功", result);
    }
}