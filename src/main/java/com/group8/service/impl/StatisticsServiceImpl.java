package com.group8.service.impl;

import com.group8.entity.Result;
import com.group8.entity.UserRoleStatistics;
import com.group8.entity.ProductCategoryStatistics;
import com.group8.mapper.StatisticsMapper;
import com.group8.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {


    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    @Cacheable(
            cacheNames = "userRoleCount",
            key = "'all'",
            unless = "#result == null ||#result.code==0"
    )
    public Result getUserCountByRole() {
        try {
            log.info("查询用户角色统计:回源数据库");
            List<UserRoleStatistics> stats = statisticsMapper.getUserCountByRole();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取用户角色统计失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(
            cacheNames = "productCategoryCount",
            key = "'all'",
            unless = "#result == null ||#result.code==0"
    )
    public Result getProductCountByCategory() {
        try {
            log.info("查询商品类别统计: 回源数据库");
            List<ProductCategoryStatistics> stats = statisticsMapper.getProductCountByCategory();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取商品类别统计失败: " + e.getMessage());
        }
    }
}
