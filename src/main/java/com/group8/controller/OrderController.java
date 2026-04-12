package com.group8.controller;

import com.group8.dto.*;
import com.group8.entity.CustomUserDetails;
import com.group8.entity.Order;
import com.group8.entity.Result;
import com.group8.service.OrderService;
import com.group8.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletService walletService;

    /**
     * 采购商创建订单,权限: 仅采购商角色(userType=1)可调用,需携带有效Token
     */
    @PostMapping
    @PreAuthorize("hasRole('PURCHASER')")
    public Result createOrder(@RequestBody OrderCreateRequest request, Authentication authentication) {
        log.info("采购商创建订单：{}", request);
        Long buyerId = getCurrentUserId(authentication);
        OrderCreateResponse response = orderService.createOrder(request, buyerId);
        return Result.success(response);
    }

    /**
     * 获取订单列表,支持分页和多种筛选条件. 权限: 需登录Token,根据用户角色返回相应数据
     */
    @GetMapping
    public Result getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        Integer userType = getCurrentUserType(authentication);
        
        OrderQueryRequest queryRequest = new OrderQueryRequest();
        queryRequest.setPage(page);
        queryRequest.setLimit(limit);
        queryRequest.setOrderSn(orderSn);
        queryRequest.setStatus(status);
        queryRequest.setStartTime(startTime);
        queryRequest.setEndTime(endTime);
        
        log.info("获取订单列表，用户ID：{}，用户类型：{}", userId, userType);
        return orderService.getOrders(queryRequest, userId, userType);
    }

    /**
     * 根据订单ID获取订单详细信息. 权限: 需登录Token,供应商的待处理订单详情,采购商的待支付订单详情
     */
    @GetMapping("/{orderId}")
    public Result getOrderDetail(@PathVariable Long orderId, Authentication authentication) {
        log.info("获取订单详情，订单ID：{}", orderId);
        Long userId = getCurrentUserId(authentication);

        // 使用带订单项的查询方法
        Order order = orderService.getOrderWithItems(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 检查权限
        if (!order.getBuyerId().equals(userId) && !order.getSupplierId().equals(userId)) {
            return Result.error("无权查看此订单");
        }

        return Result.success(order);
    }

    /**
     * 采购商更新订单信息(如收货地址、备注等). 权限: 仅采购商可调用,且只能更新待支付状态的订单
     */
    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result updateOrder(@PathVariable Long orderId, 
                             @RequestBody OrderUpdateRequest request, 
                             Authentication authentication) {
        log.info("更新订单，订单ID：{}，更新内容：{}", orderId, request);
        Long buyerId = getCurrentUserId(authentication);
        return orderService.updateOrder(orderId, request, buyerId);
    }

    /**
     * 采购商取消订单. 权限: 仅采购商可调用,且只能取消待支付订单
     */
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result cancelOrder(@PathVariable Long orderId, 
                             @RequestBody OrderCancelRequest request, 
                             Authentication authentication) {
        log.info("取消订单，订单ID：{}，取消原因：{}", orderId, request.getCancelReason());
        Long buyerId = getCurrentUserId(authentication);
        return orderService.cancelOrder(orderId, request, buyerId);
    }


    /**
     * 订单支付功能 - 将订单从待支付状态更新为待发货状态
     */
    @PostMapping("/pay/{orderId}")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result payOrder(@PathVariable Long orderId, Authentication authentication) {
        log.info("订单支付，订单ID：{}", orderId);
        Long userId = getCurrentUserId(authentication);

        return orderService.payOrder(orderId, userId);
    }


    /**
     * 供应商获取待处理订单列表,权限: 仅供应商角色(userType=2)可调用,需携带有效Token
     */
    @GetMapping("/supplier/pending")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result getPendingOrdersForSupplier(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer status,
            Authentication authentication) {
        
        Long supplierId = getCurrentUserId(authentication);
        
        OrderQueryRequest queryRequest = new OrderQueryRequest();
        queryRequest.setPage(page);
        queryRequest.setLimit(limit);
        queryRequest.setStatus(status);
        
        log.info("供应商获取待处理订单，供应商ID：{}", supplierId);
        return orderService.getPendingOrdersForSupplier(queryRequest, supplierId);
    }

    /**
     * 供应商上传配送信息,权限: 仅供应商角色(userType=2)可调用,需携带有效Token
     */
    @PostMapping("/{orderId}/delivery")
    @PreAuthorize("hasRole('SUPPLIER')")
    public Result addShippingInfo(@PathVariable Long orderId, 
                                 @RequestBody ShippingInfoRequest request, 
                                 Authentication authentication) {
        log.info("供应商上传配送信息，订单ID：{}，配送信息：{}", orderId, request);
        Long supplierId = getCurrentUserId(authentication);
        return orderService.addShippingInfo(orderId, request, supplierId);
    }


    /**
     * 将所选商品加入购物车,权限: 仅采购商可调用,需携带有效token
     */
    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        log.info("添加商品到购物车，请求：{}", request);
        Long userId = getCurrentUserId(authentication);
        return orderService.addToCart(request, userId);
    }

    /**
     * 获取购物车列表,权限: 仅采购商可调用,需携带有效token
     */
    @GetMapping("/cart")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result getCartItems(Authentication authentication) {
        log.info("获取购物车列表");
        Long userId = getCurrentUserId(authentication);
        return orderService.getCartItems(userId);
    }

    /**
     * 购物车结算生成订单,权限: 仅采购商角色(userType=1)可调用,需携带有效Token
     */
    @PostMapping("/cart/checkout")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result checkout(@RequestBody CartCheckoutRequest request, Authentication authentication) {
        log.info("购物车结算，请求：{}", request);
        Long buyerId = getCurrentUserId(authentication);
        return orderService.checkout(request, buyerId);
    }


    /**
     * 从购物车移除商品,权限: 仅采购商可调用,需携带有效token
     */
    @DeleteMapping("/cart/{itemId}")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result removeFromCart(@PathVariable Long itemId, Authentication authentication) {
        log.info("从购物车移除商品，商品ID：{}", itemId);
        Long userId = getCurrentUserId(authentication);
        log.info("从购物车移除商品，用户ID：{}", userId);
        return orderService.removeFromCart(itemId, userId);
    }

    /**
     * 更新购物车商品数量,权限: 仅采购商可调用,需携带有效token
     */
    @PutMapping("/cart/{itemId}")
    @PreAuthorize("hasRole('PURCHASER')")
    public Result updateCartItem(@PathVariable Long itemId,
                                 @RequestParam Integer quantity,
                                 Authentication authentication) {
        log.info("更新购物车商品数量，商品ID：{}，数量：{}", itemId, quantity);
        Long userId = getCurrentUserId(authentication);
        return orderService.updateCartItem(itemId, quantity, userId);
    }


    // 私有方法
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("无法获取当前用户信息");
    }

    private Integer getCurrentUserType(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserType();
        }
        throw new RuntimeException("无法获取当前用户类型");
    }
}
