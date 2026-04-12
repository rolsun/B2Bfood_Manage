package com.group8.controller;

import com.group8.entity.Category;
import com.group8.entity.Result;
import com.group8.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品分类列表
     */
    @GetMapping
    public Result getCategories() {
        log.info("获取商品分类列表");
        return Result.success(productService.getCategories());
    }

    /**
     * 获取某类别下的所有对应商品信息
     */
    @GetMapping("/{categoryId}")
    public Result getProductsByCategoryId(@PathVariable("categoryId") Integer categoryId) {
        log.info("获取某类别下的所有对应商品信息");
        return Result.success(productService.getProductsByCategoryId(categoryId));
    }

    /**
     * 创建商品分类类目,仅管理员可调用
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result create(@RequestBody Category category) {
        log.info("创建商品分类类目：{}", category);
        productService.addCategory(category);
        return Result.success(category);
    }

    /**
     * 更新商品分类信息,权限: 仅管理员可调用,需携带有效Token
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateCategory(@PathVariable Integer categoryId, @RequestBody Category category) {
        log.info("更新商品分类：{}, 分类ID：{}", category, categoryId);
        productService.updateCategory(categoryId, category);
        return Result.success("分类更新成功");
    }

    /**
     * 删除商品分类,权限: 仅管理员可调用,需携带有效Token
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteCategory(@PathVariable Integer categoryId) {
        log.info("删除商品分类：{}", categoryId);
        productService.deleteCategory(categoryId);
        return Result.success("分类删除成功");
    }
}
