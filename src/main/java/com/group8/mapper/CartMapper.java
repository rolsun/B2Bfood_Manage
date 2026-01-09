package com.group8.mapper;

import com.group8.entity.CartItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {
    
    @Insert("INSERT INTO cart (user_id, product_id, quantity, note, create_time, update_time) " +
            "VALUES (#{cartItem.userId}, #{cartItem.productId}, #{cartItem.quantity}, " +
            "#{cartItem.note}, #{cartItem.createTime}, #{cartItem.updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "cartItem.id")
    int insert(@Param("cartItem") CartItem cartItem);
    

    List<CartItem> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM cart WHERE id = #{itemId} AND user_id = #{userId}")
    CartItem selectByIdAndUser(@Param("itemId") Long itemId, @Param("userId") Long userId);
    
    @Update("UPDATE cart SET quantity = #{quantity}, note = #{note}, update_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int update(@Param("id") Long id, @Param("quantity") Integer quantity, 
               @Param("note") String note, @Param("userId") Long userId);
    
    @Delete("DELETE FROM cart WHERE id = #{itemId} AND user_id = #{userId}")
    int deleteByIdAndUser(@Param("itemId") Long itemId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
