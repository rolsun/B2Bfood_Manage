package com.group8.service.impl;

import com.group8.entity.AfterSales;
import com.group8.entity.AfterSalesLog;
import com.group8.mapper.AfterSalesMapper;
import com.group8.mapper.AfterSalesLogMapper;
import com.group8.service.AfterSalesService;
import com.group8.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 售后服务实现类
 */
@Service
public class AfterSalesServiceImpl implements AfterSalesService {
    
    @Autowired
    private AfterSalesMapper afterSalesMapper;
    
    @Autowired
    private AfterSalesLogMapper afterSalesLogMapper;
    
    @Override
    @Transactional
    public AfterSales apply(AfterSales afterSales) {
        if (afterSales == null) {
            throw new BusinessException("售后申请信息不能为空");
        }
        if (afterSales.getOrderId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (afterSales.getBuyerId() == null) {
            throw new BusinessException("采购商ID不能为空");
        }
        if (afterSales.getSellerId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        if (afterSales.getAfterSalesType() == null) {
            throw new BusinessException("售后类型不能为空");
        }
        if (afterSales.getReason() == null || afterSales.getReason().isEmpty()) {
            throw new BusinessException("申请原因不能为空");
        }
        
        // 设置默认状态为待审核
        afterSales.setStatus(1);
        
        int result = afterSalesMapper.create(afterSales);
        if (result != 1) {
            throw new BusinessException("提交售后申请失败");
        }
        
        // 记录操作日志
        createLog(afterSales.getId(), afterSales.getBuyerId(), "采购商提交售后申请");
        
        return afterSales;
    }
    
    @Override
    public AfterSales getById(Integer id, Integer userId) {
        if (id == null) {
            throw new BusinessException("售后申请ID不能为空");
        }
        
        AfterSales afterSales = afterSalesMapper.getById(id);
        if (afterSales == null) {
            throw new BusinessException("售后申请不存在");
        }
        
        // 验证用户权限，只能查看自己相关的售后申请
        if (!afterSales.getBuyerId().equals(userId) && !afterSales.getSellerId().equals(userId)) {
            throw new BusinessException("无权查看此售后申请");
        }
        
        return afterSales;
    }
    
    @Override
    public List<AfterSales> list(Map<String, Object> params) {
        return afterSalesMapper.list(params);
    }
    
    @Override
    public int count(Map<String, Object> params) {
        return afterSalesMapper.count(params);
    }
    
    @Override
    @Transactional
    public AfterSales cancel(Integer id, Integer userId) {
        if (id == null) {
            throw new BusinessException("售后申请ID不能为空");
        }
        
        AfterSales afterSales = afterSalesMapper.getById(id);
        if (afterSales == null) {
            throw new BusinessException("售后申请不存在");
        }
        
        // 验证用户权限，只能取消自己的售后申请
        if (!afterSales.getBuyerId().equals(userId)) {
            throw new BusinessException("无权取消此售后申请");
        }
        
        // 只有待审核状态的售后申请才能取消
        if (afterSales.getStatus() != 1) {
            throw new BusinessException("售后申请状态不允许取消");
        }
        
        // 更新状态为已关闭
        afterSales.setStatus(7);
        int result = afterSalesMapper.update(afterSales);
        if (result != 1) {
            throw new BusinessException("取消售后申请失败");
        }
        
        // 记录操作日志
        createLog(id, userId, "采购商取消售后申请");
        
        return afterSales;
    }
    
    @Override
    @Transactional
    public AfterSales review(Integer id, Integer reviewResult, String reviewComment, Double actualRefundAmount, Integer userId) {
        if (id == null) {
            throw new BusinessException("售后申请ID不能为空");
        }
        if (reviewResult == null) {
            throw new BusinessException("审核结果不能为空");
        }
        if (reviewComment == null || reviewComment.isEmpty()) {
            throw new BusinessException("审核意见不能为空");
        }
        
        AfterSales afterSales = afterSalesMapper.getById(id);
        if (afterSales == null) {
            throw new BusinessException("售后申请不存在");
        }
        
        // 验证用户权限，只能审核自己的售后申请
        if (!afterSales.getSellerId().equals(userId)) {
            throw new BusinessException("无权审核此售后申请");
        }
        
        // 只有待审核状态的售后申请才能审核
        if (afterSales.getStatus() != 1) {
            throw new BusinessException("售后申请状态不允许审核");
        }
        
        // 更新售后申请信息
        afterSales.setReviewComment(reviewComment);
        
        if (reviewResult == 1) {
            // 审核通过
            afterSales.setStatus(2); // 审核通过
            
            // 根据售后类型设置不同的状态
            if (afterSales.getAfterSalesType() == 1) {
                // 退货退款，设置为待退货
                afterSales.setStatus(4);
            } else if (afterSales.getAfterSalesType() == 2) {
                // 仅退款，设置为已完成
                afterSales.setStatus(6);
            } else if (afterSales.getAfterSalesType() == 3) {
                // 换货，设置为待退货
                afterSales.setStatus(4);
            }
        } else {
            // 审核拒绝
            afterSales.setStatus(3); // 审核拒绝
        }
        
        int result = afterSalesMapper.update(afterSales);
        if (result != 1) {
            throw new BusinessException("审核售后申请失败");
        }
        
        // 记录操作日志
        String logContent = reviewResult == 1 ? "供应商审核通过" : "供应商审核拒绝";
        createLog(id, userId, logContent + "，意见：" + reviewComment);
        
        return afterSales;
    }
    
    @Override
    @Transactional
    public AfterSales uploadReturnInfo(Integer id, String shippingCompany, String trackingNumber, String returnNote, Integer userId) {
        // 此方法已简化，不再实现具体逻辑
        throw new BusinessException("此功能已简化，暂不支持");
    }
    
    @Override
    @Transactional
    public AfterSales confirmReceive(Integer id, Integer receiveStatus, String receiveNote, Integer userId) {
        // 此方法已简化，不再实现具体逻辑
        throw new BusinessException("此功能已简化，暂不支持");
    }
    
    @Override
    public List<AfterSales> getPendingList(Map<String, Object> params) {
        return afterSalesMapper.getPendingList(params);
    }
    
    @Override
    public int countPending(Map<String, Object> params) {
        return afterSalesMapper.countPending(params);
    }
    
    @Override
    public List<AfterSalesLog> getLogs(Integer afterSalesId) {
        if (afterSalesId == null) {
            throw new BusinessException("售后申请ID不能为空");
        }
        return afterSalesLogMapper.listByAfterSalesId(afterSalesId);
    }
    
    /**
     * 创建操作日志
     * @param afterSalesId 售后申请ID
     * @param operatorId 操作人ID
     * @param operationContent 操作内容
     */
    private void createLog(Integer afterSalesId, Integer operatorId, String operationContent) {
        AfterSalesLog log = new AfterSalesLog();
        log.setAfterSalesId(afterSalesId);
        log.setOperatorId(operatorId);
        log.setOperatorName("用户" + operatorId); // 实际项目中应从用户表获取用户名
        log.setOperationContent(operationContent);
        
        afterSalesLogMapper.create(log);
    }
}
