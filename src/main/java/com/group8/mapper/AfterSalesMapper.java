package com.group8.mapper;

import com.group8.entity.AfterSales;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 售后申请Mapper接口
 */
@Mapper
public interface AfterSalesMapper {
    /**
     * 根据ID获取售后申请信息
     * @param id 售后申请ID
     * @return 售后申请信息
     */
    AfterSales getById(@Param("id") Integer id);
    
    /**
     * 获取售后申请列表
     * @param params 查询参数
     * @return 售后申请列表
     */
    List<AfterSales> list(@Param("params") Map<String, Object> params);
    
    /**
     * 获取售后申请总数
     * @param params 查询参数
     * @return 售后申请总数
     */
    int count(@Param("params") Map<String, Object> params);
    
    /**
     * 创建售后申请
     * @param afterSales 售后申请信息
     * @return 影响行数
     */
    int create(AfterSales afterSales);
    
    /**
     * 更新售后申请信息
     * @param afterSales 售后申请信息
     * @return 影响行数
     */
    int update(AfterSales afterSales);
    
    /**
     * 更新售后申请状态
     * @param id 售后申请ID
     * @param status 售后状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 供应商获取待审核的售后申请列表
     * @param params 查询参数
     * @return 售后申请列表
     */
    List<AfterSales> getPendingList(@Param("params") Map<String, Object> params);
    
    /**
     * 供应商获取待审核的售后申请总数
     * @param params 查询参数
     * @return 售后申请总数
     */
    int countPending(@Param("params") Map<String, Object> params);
}