package com.group8.controller;

import com.group8.entity.BuyerQueryParam;
import com.group8.entity.CustomUserDetails;
import com.group8.entity.Product;
import com.group8.entity.Result;
import com.group8.mapper.ProductMapper;
import com.group8.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 供应商创建新的供货商品,权限: 仅供应商角色(userType=2)可调用,需携带有效Token
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('SUPPLIER')")  // 添加权限控制
    public Result add(@RequestBody Product product, Authentication authentication) {
        log.info("供应商创建商品：{}", product);
        Long supplierId = getCurrentUserId(authentication);
        product.setSupplierId(supplierId);
        productService.add(product);
        return Result.success(product);
    }

    /**
     * 供应商查看自己创建的所有商品,仅供应商角色可调用,需携带有效Token
     */
    @PreAuthorize("hasRole('SUPPLIER')")
    @GetMapping("/supplier")
    public Result getSupplierProducts(Authentication authentication)
    {
        Long userId = getCurrentUserId(authentication);
        log.info("供应商查看自有商品列表，用户ID：{}", userId);
        List<Product> products = productService.getSupplierProducts(userId, 2);
        return Result.success(products);
    }

    /**
     * 供应商更新自己创建的商品信息,权限: 仅商品所属供应商可调用,需携带有效Token
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result update(@PathVariable Integer productId,
                         @RequestBody Product product,
                         Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        log.info("供应商更新商品：{}, 用户ID：{}", product, userId);

        if (!isProductOwner(productId, userId)) {
            return Result.error("无权限修改此商品");
        }

        productService.update(productId, product);
        return Result.success(product);
    }

    /**
     * 供应商删除自有商品,权限: 仅商品所属供应商可调用,需携带有效Token
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result delete(@PathVariable Integer productId,Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        log.info("供应商删除自有商品：{}, 用户ID：{}", productId, userId);

        if (!isProductOwner(productId, userId)) {
            return Result.error("无权限删除此商品");
        }

        productService.delete(productId);
        return Result.success();
    }

    /**
     * 采购商获取所有商品列表,分页查询,可根据keyword模糊搜索关键词,可按商品类目ID筛选
     * 使用BuyerQueryParam类
     */
    @GetMapping
    @PreAuthorize("hasRole('PURCHASER')")
    public Result getAllProducts(BuyerQueryParam queryParam) {
        log.info("获取商品列表：{}", queryParam);
        List<Product> products = productService.getAllProducts(queryParam);
        return Result.success(products);
    }

    //私有方法
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }


    private boolean isProductOwner(Integer productId, Long userId) {
        List<Product> products = productMapper.selectBySupplierId(userId);
        return products.stream().anyMatch(p -> p.getProductId().equals(productId));
    }



















}
