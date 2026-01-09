package com.group8.controller;

import com.group8.entity.Result;
import com.group8.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取用户角色统计
     * 权限: 所有登录用户可见
     */
    @GetMapping("/user-role-count")
    public Result getUserCountByRole() {
        log.info("获取用户角色统计");
        return statisticsService.getUserCountByRole();
    }

    /**
     * 获取商品类别统计
     * 权限: 所有登录用户可见
     */
    @GetMapping("/product-category-count")
    public Result getProductCountByCategory() {
        log.info("获取商品类别统计");
        return statisticsService.getProductCountByCategory();
    }

    /**
     * 获取综合统计数据
     * 权限: 所有登录用户可见
     */
    @GetMapping("/overview")
    public Result getOverviewStatistics() {
        log.info("获取综合统计数据");
        try {
            var userStats = statisticsService.getUserCountByRole();
            var productStats = statisticsService.getProductCountByCategory();

            var overview = new java.util.HashMap<String, Object>();
            overview.put("userRoleStats", userStats.getData());
            overview.put("productCategoryStats", productStats.getData());

            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取综合统计数据失败: " + e.getMessage());
        }
    }
}
