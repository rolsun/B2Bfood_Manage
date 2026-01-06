package com.group8.mapper;

import com.group8.entity.Statistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 数据统计Mapper接口
 */
@Mapper
public interface StatisticsMapper {
    /**
     * 获取销售统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getSalesStatistics(@Param("params") Map<String, Object> params);
    
    /**
     * 获取销售趋势
     * @param params 查询参数
     * @return 统计结果
     */
    List<Map<String, Object>> getSalesTrend(@Param("params") Map<String, Object> params);
    
    /**
     * 获取用户统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getUserStatistics(@Param("params") Map<String, Object> params);
    
    /**
     * 获取交易统计
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Object> getTransactionStatistics(@Param("params") Map<String, Object> params);
}