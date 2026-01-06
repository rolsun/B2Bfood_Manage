package com.group8.mapper;

import com.group8.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {
    /**
     * 根据ID获取角色信息
     * @param id 角色ID
     * @return 角色信息
     */
    Role getById(@Param("id") Integer id);
    
    /**
     * 获取角色列表
     * @return 角色列表
     */
    List<Role> list();
    
    /**
     * 根据用户ID获取角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> listByUserId(@Param("userId") Integer userId);
    
    /**
     * 创建角色
     * @param role 角色信息
     * @return 影响行数
     */
    int create(Role role);
    
    /**
     * 更新角色信息
     * @param role 角色信息
     * @return 影响行数
     */
    int update(Role role);
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 影响行数
     */
    int delete(@Param("id") Integer id);
    
    /**
     * 给用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int assignRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
    
    /**
     * 取消用户角色
     * @param userId 用户ID
     * @return 影响行数
     */
    int unassignRole(@Param("userId") Integer userId);
}