package com.group8.mapper;

import com.group8.dto.UserInfoUpdateRequest;
import com.group8.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE userName = #{userName}")
    User findByUsername(@Param("userName") String userName);
    
    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM users WHERE userId = #{userId}")
    User findById(@Param("userId") Integer userId);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO users (userName, password, userType) VALUES (#{userName}, #{password}, #{userType})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET password = #{password}, userType = #{userType} WHERE userId = #{userId}")
    void update(User user);


    //以下为用户管理里的功能接口
    @Select("SELECT * FROM users WHERE userId = #{userId}")
    User selectById(@Param("userId") Long userId);


    int updateUserInfo(@Param("userId") Long userId,
                       @Param("request") UserInfoUpdateRequest request);

    List<User> selectUsers(Map<String, Object> params);

    int countUsers(Map<String, Object> params);

    @Delete("DELETE FROM users WHERE userId = #{userId}")
    int deleteById(@Param("userId") Long userId);

    @Delete("DELETE FROM products WHERE supplier_id = #{userId}")
    int deleteSupplierProducts(@Param("userId") Long userId);

    @Delete("DELETE FROM orders WHERE buyer_id = #{userId} OR supplier_id = #{userId}")
    int deleteBuyerOrders(@Param("userId") Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteBuyerCart(@Param("userId") Long userId);
}
