package com.group8.mapper;

import com.group8.entity.User;
import org.apache.ibatis.annotations.*;

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
}
