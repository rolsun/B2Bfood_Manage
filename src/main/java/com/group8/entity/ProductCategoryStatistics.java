package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCategoryStatistics {
    private Integer categoryId;  // 分类ID - 与数据库 products.categoryId 类型匹配
    private String categoryName; // 分类名称 - 与数据库 categories.name 字段匹配
    private Long count;          // 商品数量
}
