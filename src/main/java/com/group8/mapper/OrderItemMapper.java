package com.group8.mapper;

import com.group8.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Insert("INSERT INTO order_items (order_id, product_id, quantity, price, total_price, note, create_time) " +
            "VALUES (#{item.orderId}, #{item.productId}, #{item.quantity}, #{item.price}, #{item.totalPrice}, " +
            "#{item.note}, #{item.createTime})")
    int insert(@Param("item") OrderItem item);
    
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
}
