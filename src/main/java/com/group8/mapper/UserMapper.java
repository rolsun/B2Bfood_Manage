package com.group8.mapper;

import com.group8.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(@Param("id") Integer id);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(@Param("username") String username);
    
    /**
     * 获取用户列表
     * @param params 查询参数
     * @return 用户列表
     */
    List<User> list(@Param("params") Map<String, Object> params);
    
    /**
     * 获取用户总数
     * @param params 查询参数
     * @return 用户总数
     */
    int count(@Param("params") Map<String, Object> params);
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 用户状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int delete(@Param("id") Integer id);
}