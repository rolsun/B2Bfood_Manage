package com.group8.service;

import com.group8.entity.AfterSales;
import com.group8.entity.Result;

public interface AfterSalesService {
    
    /**
     * 提交售后申请
     */
    Result submitAfterSales(AfterSales afterSales, String username);
    
    /**
     * 处理售后申请（供应商操作）
     */
    Result processAfterSales(Long id, String username);
    
    /**
     * 取消售后申请（采购商操作）
     */
    Result cancelAfterSales(Long id, String username);
    
    /**
     * 查询售后申请详情
     */
    Result getAfterSalesDetail(Long id, String username);
    
    /**
     * 获取供应商待处理的售后申请列表
     */
    Result getPendingAfterSalesForSupplier(String supplierId);
    
    /**
     * 获取采购商提交的售后申请列表
     */
    Result getAfterSalesForBuyer(Long buyerId);
}
