package com.group8.service.impl;

import com.group8.entity.Result;
import com.group8.entity.UserRoleStatistics;
import com.group8.entity.ProductCategoryStatistics;
import com.group8.mapper.StatisticsMapper;
import com.group8.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Result getUserCountByRole() {
        try {
            List<UserRoleStatistics> stats = statisticsMapper.getUserCountByRole();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取用户角色统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result getProductCountByCategory() {
        try {
            List<ProductCategoryStatistics> stats = statisticsMapper.getProductCountByCategory();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取商品类别统计失败: " + e.getMessage());
        }
    }
}
