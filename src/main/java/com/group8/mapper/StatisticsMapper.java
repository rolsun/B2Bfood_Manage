package com.group8.mapper;

import com.group8.entity.UserRoleStatistics;
import com.group8.entity.ProductCategoryStatistics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsMapper {
    
    /**
     * 获取用户角色统计
     */
    List<UserRoleStatistics> getUserCountByRole();
    
    /**
     * 获取商品类别统计
     */
    List<ProductCategoryStatistics> getProductCountByCategory();
}
