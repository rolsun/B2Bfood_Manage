package com.group8.mapper;

import com.group8.entity.Order;
import com.group8.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    
    @Insert("INSERT INTO orders (order_sn, buyer_id, supplier_id, total_amount, order_status, " +
            "payment_method, delivery_address_id, buyer_note, create_time, update_time) " +
            "VALUES (#{order.orderSn}, #{order.buyerId}, #{order.supplierId}, #{order.totalAmount}, " +
            "#{order.orderStatus}, #{order.paymentMethod}, #{order.deliveryAddressId}, " +
            "#{order.buyerNote}, #{order.createTime}, #{order.updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "order.id")
    int insert(@Param("order") Order order);
    
    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    Order selectById(@Param("orderId") Long orderId);
    
    @Select("SELECT * FROM orders WHERE order_sn = #{orderSn}")
    Order selectByOrderSn(@Param("orderSn") String orderSn);
    
    @Select("SELECT * FROM orders WHERE buyer_id = #{userId} OR supplier_id = #{userId}")
    List<Order> selectByUserId(@Param("userId") Long userId);
    

    default List<Order> selectPendingOrdersBySupplierId(Long supplierId) {
        return selectOrdersBySupplierAndStatus(supplierId, 2, 0, Integer.MAX_VALUE);
    }

    @Update("UPDATE orders SET order_status = #{orderStatus}, update_time = NOW() WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("orderStatus") Integer orderStatus);
    
    @Update("UPDATE orders SET delivery_address_id = #{deliveryAddressId}, buyer_note = #{buyerNote}, update_time = NOW() WHERE id = #{orderId}")
    int updateOrder(@Param("orderId") Long orderId, 
                   @Param("deliveryAddressId") Long deliveryAddressId,
                   @Param("buyerNote") String buyerNote);
    
    @Update("UPDATE orders SET cancel_reason = #{cancelReason}, order_status = 5, update_time = NOW() WHERE id = #{orderId}")
    int cancelOrder(@Param("orderId") Long orderId, @Param("cancelReason") String cancelReason);
    
    @Update("UPDATE orders SET shipping_company = #{shippingCompany}, tracking_number = #{trackingNumber}, " +
            "estimated_delivery_time = #{estimatedDeliveryTime}, order_status = 3, update_time = NOW() WHERE id = #{orderId}")
    int updateShippingInfo(@Param("orderId") Long orderId, 
                          @Param("shippingCompany") String shippingCompany,
                          @Param("trackingNumber") String trackingNumber,
                          @Param("estimatedDeliveryTime") String estimatedDeliveryTime);
    
    List<Order> selectOrders(Map<String, Object> params);
    
    int countOrders(Map<String, Object> params);

    Order selectOrderWithItems(@Param("orderId") Long orderId);


    List<Order> selectOrdersBySupplierAndStatus(@Param("supplierId") Long supplierId,
                                                @Param("status") Integer status,
                                                @Param("page") int page,
                                                @Param("limit") int limit);

    int countOrdersBySupplierAndStatus(@Param("supplierId") Long supplierId,
                                       @Param("status") Integer status);

//获取订单表中的supplierId
    @Select("SELECT supplier_id FROM orders WHERE order_sn = #{orderNumber}")
    int selectSupplierIdByOrderSn(String orderNumber);
}