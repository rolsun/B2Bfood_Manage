package com.group8.mapper;

import com.group8.entity.BuyerQueryParam;
import com.group8.entity.Category;
import com.group8.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    /**
     * 供应商创建商品
     * @param product
     */
    void add(Product product);

    /**
     * 供应商获取自有商品列表
     * @param userId
     * @return
     */

    List<Product> selectBySupplierId(Long userId);



    /** * 供应商更新商品
     * @param productId
     * @param product
     **/
    void update(@Param("productId") Integer productId,  @Param("product") Product product);

    /**
     * 供应商删除商品
     */
    @Delete("delete from products where productId=#{productId}")
    void delete(Integer productId);

    /**
     * 采购商获取所有商品列表,分页查询,可根据keyword模糊搜索关键词,可按商品类目ID筛选
     */
    List<Product> getAllProducts(@Param("queryParam") BuyerQueryParam queryParam);



    /**
     * 获取商品分类列表
     */
    @Select("select * from categories")
    List<Category> getCategories();

    /**
     * 获取某类别下的所有对应商品信息
     */
    List<Product> getProductsByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 创建商品分类类目
     */
    @Insert("insert into categories(name) values(#{name})")
    void addCategory(Category category);


    /**
     * 更新商品分类类目
     */
    @Update("UPDATE categories SET name = #{name} WHERE id = #{id}")
    void updateCategory(@Param("id") Integer id, @Param("name") String name);

    /**
     * 删除商品分类类目
     * @param categoryId
     */
    @Delete("DELETE FROM categories WHERE id = #{categoryId}")
    void deleteCategory(Integer categoryId);
}
