package com.group8.service;

import com.group8.entity.AfterSales;
import com.group8.entity.AfterSalesLog;
import java.util.Map;
import java.util.List;

/**
 * 售后服务接口
 */
public interface AfterSalesService {
    /**
     * 提交售后申请
     * @param afterSales 售后申请信息
     * @return 售后申请信息
     */
    AfterSales apply(AfterSales afterSales);
    
    /**
     * 获取售后申请详情
     * @param id 售后申请ID
     * @param userId 当前用户ID
     * @return 售后申请详情
     */
    AfterSales getById(Integer id, Integer userId);
    
    /**
     * 获取售后申请列表
     * @param params 查询参数
     * @return 售后申请列表
     */
    List<AfterSales> list(Map<String, Object> params);
    
    /**
     * 获取售后申请总数
     * @param params 查询参数
     * @return 售后申请总数
     */
    int count(Map<String, Object> params);
    
    /**
     * 取消售后申请
     * @param id 售后申请ID
     * @param userId 当前用户ID
     * @return 售后申请信息
     */
    AfterSales cancel(Integer id, Integer userId);
    
    /**
     * 供应商审核售后申请
     * @param id 售后申请ID
     * @param reviewResult 审核结果: 1-通过, 2-拒绝
     * @param reviewComment 审核意见
     * @param actualRefundAmount 实际退款金额
     * @param userId 当前用户ID
     * @return 售后申请信息
     */
    AfterSales review(Integer id, Integer reviewResult, String reviewComment, Double actualRefundAmount, Integer userId);
    
    /**
     * 采购商上传退货信息
     * @param id 售后申请ID
     * @param shippingCompany 物流公司
     * @param trackingNumber 运单号
     * @param returnNote 退货备注
     * @param userId 当前用户ID
     * @return 售后申请信息
     */
    AfterSales uploadReturnInfo(Integer id, String shippingCompany, String trackingNumber, String returnNote, Integer userId);
    
    /**
     * 供应商确认收货
     * @param id 售后申请ID
     * @param receiveStatus 收货状态: 1-已收货, 2-商品有问题
     * @param receiveNote 收货备注
     * @param userId 当前用户ID
     * @return 售后申请信息
     */
    AfterSales confirmReceive(Integer id, Integer receiveStatus, String receiveNote, Integer userId);
    
    /**
     * 供应商获取待审核的售后申请列表
     * @param params 查询参数
     * @return 售后申请列表
     */
    List<AfterSales> getPendingList(Map<String, Object> params);
    
    /**
     * 供应商获取待审核的售后申请总数
     * @param params 查询参数
     * @return 售后申请总数
     */
    int countPending(Map<String, Object> params);
    
    /**
     * 获取售后操作日志
     * @param afterSalesId 售后申请ID
     * @return 操作日志列表
     */
    List<AfterSalesLog> getLogs(Integer afterSalesId);
}