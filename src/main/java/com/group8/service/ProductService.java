package com.group8.service;

import com.group8.entity.BuyerQueryParam;
import com.group8.entity.Category;
import com.group8.entity.Product;

import java.util.List;

public interface ProductService {

    /**
     * 供应商创建商品
     */
    void add(Product product);


    /**
     * 供应商获取自有商品列表
     *
     * @param
     * @return
     */
    List<Product> getSupplierProducts(Long userId, Integer userType);

    /**
     * 供应商更新商品
     *
     * @param productId
     * @param product
     */
    void update(Integer productId, Product product);

    /**
     * 供应商删除商品
     */
    void delete(Integer productId);

    // 添加统计商品总数方法
    int countAllProducts(BuyerQueryParam queryParam);


    /**
     * 采购商获取所有商品列表,分页查询,可根据keyword模糊搜索关键词,可按商品类目ID筛选
     */
    List<Product> getAllProducts(BuyerQueryParam queryParam);


    /**
     * 获取商品分类列表
     *
     * @return
     */

    List<Category> getCategories();

    /**
     * 获取某类别下的所有对应商品信息
     */
    List<Product> getProductsByCategoryId(Integer categoryId);

    /**
     * 创建商品分类类目
     */
    void addCategory(Category category);

    /**
     * 更新商品分类类目
     */
    void updateCategory(Integer categoryId, Category category);

    /**
     * 删除商品分类类目
     * @param categoryId
     */
    void deleteCategory(Integer categoryId);
}