package com.group8.service.impl;

import com.group8.dto.*;
import com.group8.entity.*;
import com.group8.mapper.CartMapper;
import com.group8.mapper.OrderMapper;
import com.group8.mapper.OrderItemMapper;
import com.group8.mapper.ProductMapper;
import com.group8.service.OrderService;
import com.group8.service.ProductService;
import com.group8.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private WalletService walletService;


    @Override
    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request, Long buyerId) {
        log.info("创建订单：{}", request);

        // 1. 校验库存和价格
        validateInventoryAndPrice(request.getItems());

        // 2. 计算总金额
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());

        // 3. 创建订单
        Order order = new Order();
        order.setOrderSn(generateOrderSn());
        order.setBuyerId(buyerId);
        order.setSupplierId(getSupplierId(request.getItems()));
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(1); // 待支付
        order.setPaymentMethod(1); // 固定为在线支付
        order.setDeliveryAddressId(request.getDeliveryAddressId());
        order.setBuyerNote(request.getBuyerNote());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 4. 创建订单项
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            BigDecimal price = getProductPrice(itemRequest.getProductId());
            item.setPrice(price);
            item.setTotalPrice(price.multiply(new BigDecimal(itemRequest.getQuantity())));
            item.setNote(itemRequest.getNote());
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }

        // 5. 返回结果 - 为了兼容旧代码，这里仍然返回单订单格式
        OrderCreateResponse response = new OrderCreateResponse();
        List<OrderCreateResponse.OrderInfo> orderInfos = new ArrayList<>();
        OrderCreateResponse.OrderInfo orderInfo = new OrderCreateResponse.OrderInfo(
                order.getId(),
                order.getOrderSn(),
                order.getTotalAmount().doubleValue(),
                order.getOrderStatus(),
                order.getSupplierId(),
                order.getCreateTime()
        );
        orderInfos.add(orderInfo);
        response.setOrders(orderInfos);
        response.setTotalOrderCount(1);

        log.info("订单创建成功：{}", response);
        return response;
    }

    @Override
    public Result getOrders(OrderQueryRequest queryRequest, Long userId, Integer userType) {
        log.info("获取订单列表，用户ID：{}，用户类型：{}", userId, userType);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("userType", userType);
        params.put("page", (queryRequest.getPage() - 1) * queryRequest.getLimit());
        params.put("limit", queryRequest.getLimit());
        params.put("orderSn", queryRequest.getOrderSn());
        params.put("status", queryRequest.getStatus());
        params.put("startTime", queryRequest.getStartTime());
        params.put("endTime", queryRequest.getEndTime());

        List<Order> orders = orderMapper.selectOrders(params);
        int total = orderMapper.countOrders(params);

        // 构建分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", orders);

        return Result.success(result);
    }

    @Override
    public Result getOrderDetail(Long orderId, Long userId) {
        log.info("获取订单详情，订单ID：{}，用户ID：{}", orderId, userId);

        // 使用带订单项的查询方法
        Order order = orderMapper.selectOrderWithItems(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 检查权限
        if (!order.getBuyerId().equals(userId) && !order.getSupplierId().equals(userId)) {
            return Result.error("无权查看此订单");
        }

        return Result.success(order);
    }


    @Override
    public Result updateOrder(Long orderId, OrderUpdateRequest request, Long buyerId) {
        log.info("更新订单，订单ID：{}，用户ID：{}", orderId, buyerId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getBuyerId().equals(buyerId)) {
            return Result.error("无权修改此订单");
        }

        if (order.getOrderStatus() != 1) { // 只能修改待支付状态的订单
            return Result.error("订单状态不允许修改");
        }

        orderMapper.updateOrder(orderId, request.getDeliveryAddressId(), request.getBuyerNote());

        Order updatedOrder = orderMapper.selectById(orderId);
        return Result.success(updatedOrder);
    }

    @Override
    public Result cancelOrder(Long orderId, OrderCancelRequest request, Long buyerId) {
        log.info("取消订单，订单ID：{}，用户ID：{}", orderId, buyerId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getBuyerId().equals(buyerId)) {
            return Result.error("无权取消此订单");
        }

        if (order.getOrderStatus() != 1) { // 只能取消待支付状态的订单
            return Result.error("订单状态不允许取消");
        }

        orderMapper.cancelOrder(orderId, request.getCancelReason());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("orderStatus", 5); // 已取消
        result.put("updateTime", LocalDateTime.now().toString());

        return Result.success(result);
    }

    @Override
    @Transactional
    public Result payOrder(Long orderId, Long userId) {
        log.info("订单支付，订单ID：{}，用户ID：{}", orderId, userId);

        // 验证订单属于当前用户且状态为待支付
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            return Result.error("无权支付此订单");
        }

        if (order.getOrderStatus() != 1) { // 不是待支付状态
            return Result.error("订单状态不允许支付");
        }

        // 使用钱包支付
        WalletPayRequest walletRequest = new WalletPayRequest();
        walletRequest.setAmount(order.getTotalAmount());
        walletRequest.setOrderId(orderId);
        walletRequest.setDescription("订单支付");

        Result walletResult = walletService.pay(walletRequest, userId);
        if (walletResult.getCode() != 1) {
            return walletResult; // 支付失败
        }

        // 支付成功后更新订单状态为待发货
        orderMapper.updateStatus(orderId, 2); // 2-待发货

        return Result.success("订单支付成功，等待供应商发货");
    }

    @Override
    public Result getPendingOrdersForSupplier(OrderQueryRequest queryRequest, Long supplierId) {
        log.info("供应商获取订单，供应商ID：{}，状态：{}", supplierId, queryRequest.getStatus());

        // 如果未指定状态，默认查询待发货状态(2)
        Integer status = queryRequest.getStatus() != null ? queryRequest.getStatus() : 2;

        int page = (queryRequest.getPage() - 1) * queryRequest.getLimit();
        List<Order> orders = orderMapper.selectOrdersBySupplierAndStatus(supplierId, status, page, queryRequest.getLimit());
        int total = orderMapper.countOrdersBySupplierAndStatus(supplierId, status);

        // 构建分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", orders);

        return Result.success(result);
    }




    @Override
    public Result addShippingInfo(Long orderId, ShippingInfoRequest request, Long supplierId) {
        log.info("供应商上传配送信息，订单ID：{}，供应商ID：{}", orderId, supplierId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!order.getSupplierId().equals(supplierId)) {
            return Result.error("无权为该订单发货");
        }

        if (order.getOrderStatus() != 2) { // 只能对待发货状态的订单发货
            return Result.error("订单状态不允许发货");
        }

        orderMapper.updateShippingInfo(orderId, request.getShippingCompany(),
                request.getTrackingNumber(), request.getEstimatedDeliveryTime());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("orderStatus", 3); // 已发货
        result.put("updateTime", LocalDateTime.now().toString());

        return Result.success(result);
    }

    @Override
    public Order getOrderWithItems(Long orderId) {
        return orderMapper.selectOrderWithItems(orderId);
    }



    @Override
    public Result addToCart(AddToCartRequest request, Long userId) {
        log.info("添加商品到购物车：用户ID {}，商品ID {}，数量 {}", userId, request.getProductId(), request.getQuantity());

        // 验证商品是否存在
        Product product = getProductById(request.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }

        // 验证库存
        if (product.getStock() < request.getQuantity()) {
            return Result.error("商品库存不足");
        }

        // 检查购物车中是否已有相同商品
        List<CartItem> existingItems = cartMapper.selectByUserId(userId);
        CartItem existingItem = existingItems.stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // 如果已存在，更新数量
            Integer newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                return Result.error("商品库存不足");
            }

            cartMapper.update(existingItem.getId(), newQuantity, request.getNote(), userId);
            return Result.success("购物车商品数量已更新");
        } else {
            // 如果不存在，新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setNote(request.getNote());
            cartItem.setCreateTime(java.time.LocalDateTime.now());
            cartItem.setUpdateTime(java.time.LocalDateTime.now());

            cartMapper.insert(cartItem);
            return Result.success("商品已添加到购物车");
        }
    }

    @Override
    public Result getCartItems(Long userId) {
        log.info("获取购物车列表，用户ID：{}", userId);

        List<CartItem> cartItems = cartMapper.selectByUserId(userId);
        return Result.success(cartItems);
    }

    @Override
    public Result removeFromCart(Long itemId, Long userId) {
        log.info("从购物车移除商品，商品ID：{}，用户ID：{}", itemId, userId);

        int result = cartMapper.deleteByIdAndUser(itemId, userId);
        if (result > 0) {
            return Result.success("商品已从购物车移除");
        } else {
            return Result.error("无法移除购物车商品，可能商品不存在或不属于当前用户");
        }
    }

    @Override
    public Result updateCartItem(Long itemId, Integer quantity, Long userId) {
        log.info("更新购物车商品数量，商品ID：{}，数量：{}，用户ID：{}", itemId, quantity, userId);

        CartItem cartItem = cartMapper.selectByIdAndUser(itemId, userId);
        if (cartItem == null) {
            return Result.error("购物车商品不存在");
        }

        // 验证库存
        Product product = getProductById(cartItem.getProductId());
        if (product == null || product.getStock() < quantity) {
            return Result.error("商品库存不足");
        }

        cartMapper.update(itemId, quantity, cartItem.getNote(), userId);
        return Result.success("购物车商品数量已更新");
    }


    // 购物车结算
    @Override
    public Result checkout(CartCheckoutRequest request, Long buyerId) {
        log.info("购物车结算，用户ID：{}，请求：{}", buyerId, request);

        // 获取用户购物车中的商品
        List<CartItem> cartItems = cartMapper.selectByUserId(buyerId);
        if (cartItems.isEmpty()) {
            return Result.error("购物车为空");
        }

        // 验证购物车中商品的库存和价格
        for (CartItem cartItem : cartItems) {
            Product product = getProductById(cartItem.getProductId());
            if (product == null) {
                return Result.error("商品不存在，ID：" + cartItem.getProductId());
            }

            if (product.getStock() < cartItem.getQuantity()) {
                return Result.error("商品库存不足，商品ID：" + cartItem.getProductId());
            }
        }

        // 按供应商分组购物车商品 - 修正类型
        Map<Long, List<CartItem>> groupedBySupplier = cartItems.stream()
                .collect(Collectors.groupingBy(cartItem -> {
                    Product product = getProductById(cartItem.getProductId());
                    return product.getSupplierId(); // supplierId 是 Long 类型
                }));
        // 为每个供应商创建订单
        List<OrderCreateResponse.OrderInfo> orderInfos = new ArrayList<>();
        for (Map.Entry<Long, List<CartItem>> entry : groupedBySupplier.entrySet()) {
            Long supplierId = entry.getKey();
            List<CartItem> supplierItems = entry.getValue();

            // 构建针对该供应商的订单请求
            OrderCreateRequest orderRequest = new OrderCreateRequest();
            orderRequest.setDeliveryAddressId(request.getDeliveryAddressId());
            orderRequest.setPaymentMethod(request.getPaymentMethod());
            orderRequest.setBuyerNote(request.getBuyerNote());

            // 转换供应商商品为订单项
            List<OrderItemRequest> orderItems = new ArrayList<>();
            for (CartItem cartItem : supplierItems) {
                OrderItemRequest itemRequest = new OrderItemRequest();
                itemRequest.setProductId(cartItem.getProductId());
                itemRequest.setQuantity(cartItem.getQuantity());
                itemRequest.setNote(cartItem.getNote());
                orderItems.add(itemRequest);
            }
            orderRequest.setItems(orderItems);

            // 创建该供应商的订单
            OrderCreateResponse.OrderInfo orderInfo = createOrderForSupplier(orderRequest, buyerId, supplierId);
            orderInfos.add(orderInfo);
        }

        // 构建多订单响应
        OrderCreateResponse response = new OrderCreateResponse();
        response.setOrders(orderInfos);
        response.setTotalOrderCount(orderInfos.size());

        // 清空购物车
        cartMapper.deleteByUserId(buyerId);

        return Result.success(response);
    }





    // 辅助方法
    private void validateInventoryAndPrice(List<OrderItemRequest> items) {
        log.info("校验库存和价格：{}", items);

        for (OrderItemRequest item : items) {
            Product product = getProductById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在，ID：" + item.getProductId());
            }

            // 校验库存
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足，商品ID：" + item.getProductId() +
                        "，所需数量：" + item.getQuantity() +
                        "，可用库存：" + product.getStock());
            }
        }

        log.info("库存和价格校验通过");
    }


    private BigDecimal calculateTotalAmount(List<OrderItemRequest> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            BigDecimal price = getProductPrice(item.getProductId());
            BigDecimal itemTotal = price.multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }
        return total;
    }

    private String generateOrderSn() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = String.valueOf(System.currentTimeMillis() % 1000000);
        String randomStr = String.format("%04d", new java.util.Random().nextInt(10000));

        return "ORD" + dateStr + timeStr + randomStr;
    }

    private Long getSupplierId(List<OrderItemRequest> items) {
        if (items.isEmpty()) {
            throw new RuntimeException("订单项不能为空");
        }

        // 对于跨供应商订单，返回第一个商品的供应商ID
        Long firstProductId = items.get(0).getProductId();
        Product product = getProductById(firstProductId);
        if (product == null) {
            throw new RuntimeException("商品不存在，ID：" + firstProductId);
        }

        // 不再抛出跨供应商异常，因为现在支持跨供应商下单
        return product.getSupplierId();
    }



    private BigDecimal getProductPrice(Long productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在，ID：" + productId);
        }
        return product.getPrice();
    }


    private Product getProductById(Long productId) {
        try {
            // 使用注入的 productMapper 查询商品信息
            Product product = productMapper.selectById(productId);
            if (product == null) {
                log.warn("商品不存在，商品ID：{}", productId);
            }
            return product;
        } catch (Exception e) {
            log.error("查询商品信息失败，商品ID：{}", productId, e);
            return null;
        }
    }

    private OrderCreateResponse.OrderInfo createOrderForSupplier(OrderCreateRequest request,
                                                                 Long buyerId, Long supplierId) {
        log.info("为供应商 {} 创建订单", supplierId);

        // 1. 校验库存和价格（仅针对当前供应商的商品）
        validateInventoryAndPriceForSupplier(request.getItems());

        // 2. 计算总金额
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());

        // 3. 创建订单
        Order order = new Order();
        order.setOrderSn(generateOrderSn());
        order.setBuyerId(buyerId);
        order.setSupplierId(Long.valueOf(supplierId)); // 设置特定供应商
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(1); // 待支付
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryAddressId(request.getDeliveryAddressId());
        order.setBuyerNote(request.getBuyerNote());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 4. 创建订单项
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            BigDecimal price = getProductPrice(itemRequest.getProductId());
            item.setPrice(price);
            item.setTotalPrice(price.multiply(new BigDecimal(itemRequest.getQuantity())));
            item.setNote(itemRequest.getNote());
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }

        // 5. 返回订单信息
        return new OrderCreateResponse.OrderInfo(
                order.getId(),
                order.getOrderSn(),
                order.getTotalAmount().doubleValue(),
                order.getOrderStatus(),
                order.getSupplierId(),
                order.getCreateTime()
        );
    }


    private void validateInventoryAndPriceForSupplier(List<OrderItemRequest> items) {
        log.info("校验供应商商品库存和价格：{}", items);

        for (OrderItemRequest item : items) {
            Product product = getProductById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在，ID：" + item.getProductId());
            }

            // 校验库存
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足，商品ID：" + item.getProductId() +
                        "，所需数量：" + item.getQuantity() +
                        "，可用库存：" + product.getStock());
            }
        }

        log.info("供应商商品库存和价格校验通过");
    }

}
