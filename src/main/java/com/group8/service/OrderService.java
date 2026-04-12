package com.group8.service;

import com.group8.dto.*;
import com.group8.entity.Order;
import com.group8.entity.Result;
import java.util.List;

public interface OrderService {
    // 创建订单
    OrderCreateResponse createOrder(OrderCreateRequest request, Long buyerId);
    
    // 获取订单列表
    Result getOrders(OrderQueryRequest queryRequest, Long userId, Integer userType);
    
    // 获取订单详情
    Result getOrderDetail(Long orderId, Long userId);
    
    // 更新订单信息
    Result updateOrder(Long orderId, OrderUpdateRequest request, Long buyerId);
    
    // 取消订单
    Result cancelOrder(Long orderId, OrderCancelRequest request, Long buyerId);
    
    // 供应商获取待处理订单
    Result getPendingOrdersForSupplier(OrderQueryRequest queryRequest, Long supplierId);
    
    // 供应商上传配送信息
    Result addShippingInfo(Long orderId, ShippingInfoRequest request, Long supplierId);

    Order getOrderWithItems(Long orderId);


    // 添加到购物车
    Result addToCart(AddToCartRequest request, Long userId);

    // 获取购物车列表
    Result getCartItems(Long userId);

    // 从购物车移除商品
    Result removeFromCart(Long itemId, Long userId);

    // 更新购物车商品数量
    Result updateCartItem(Long itemId, Integer quantity, Long userId);

    // 购物车结算
    Result checkout(CartCheckoutRequest request, Long buyerId);


    // 订单支付功能
    Result payOrder(Long orderId, Long userId);



}
