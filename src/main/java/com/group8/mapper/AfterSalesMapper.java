package com.group8.mapper;

import com.group8.entity.AfterSales;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AfterSalesMapper {
    
    /**
     * 插入新的售后申请
     */
    int insert(AfterSales afterSales);
    
    /**
     * 根据ID查询售后信息
     */
    AfterSales selectById(@Param("id") Long id);
    
    /**
     * 更新售后状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 根据供应商ID查询待处理的售后申请
     */
    List<AfterSales> selectBySupplierId(@Param("supplierId") Long supplierId);
    
    /**
     * 删除售后申请（取消申请）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据采购商ID查询售后申请
     */
    List<AfterSales> selectByBuyerId(@Param("buyerId") Long buyerId);
}
