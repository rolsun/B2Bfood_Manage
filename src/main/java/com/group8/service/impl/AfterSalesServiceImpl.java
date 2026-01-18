package com.group8.service.impl;

import com.group8.entity.AfterSales;
import com.group8.entity.Order;
import com.group8.entity.Result;
import com.group8.entity.User;
import com.group8.mapper.AfterSalesMapper;
import com.group8.mapper.OrderMapper;
import com.group8.service.AfterSalesService;
import com.group8.service.UserService;
import com.group8.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional
public class AfterSalesServiceImpl implements AfterSalesService {

    @Autowired
    private AfterSalesMapper afterSalesMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;
    
    @Autowired
    private WalletService walletService;

    @Override
    public Result submitAfterSales(AfterSales afterSales, String username) {
        try {
            // 获取用户信息
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 检查用户角色是否为采购商
            if (user.getUserType() != 1) { // 1代表采购商
                return Result.error("只有采购商才能提交售后申请");
            }

            // 设置采购商ID
            afterSales.setComplainedOrder(afterSales.getComplainedOrder());

            // 确保状态字段不为 null
            if (afterSales.getStatus() == null) {
                afterSales.setStatus(1); // 设置为待处理状态
            }

            // 插入售后申请
            afterSalesMapper.insert(afterSales);

            return Result.success("售后申请提交成功");
        } catch (Exception e) {
            return Result.error("提交售后申请失败: " + e.getMessage());
        }
    }

    @Override
    public Result processAfterSales(Long id, String username) {
        try {
            // 获取用户信息
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 检查用户角色是否为供应商
            if (user.getUserType() != 2) { // 2代表供应商
                return Result.error("只有供应商才能处理售后申请");
            }

            // 查询售后记录
            AfterSales afterSales = afterSalesMapper.selectById(id);
            if (afterSales == null) {
                return Result.error("售后申请不存在");
            }

            /*// 检查是否是自己的订单
            if (!user.getUserName().equals(afterSales.getComplainedSupplier())) {
                return Result.error("无权处理此售后申请");
            }*/

            //检查是否是自己的订单
            String orderNumber = afterSales.getComplainedOrder();//获取售后表中该条售后对应的订单号
            int supplierId = orderMapper.selectSupplierIdByOrderSn(orderNumber);//获取订单表中order_sn = orderNumber的订单下的supplierId
            //如果supplierId不等于当前登录的用户的userId,则返回"无权处理此售后申请"
            if (supplierId != user.getUserId()){
                return Result.error("无权处理此售后申请");
            }

            // 检查状态是否为待处理
            if (afterSales.getStatus() != 1) {
                return Result.error("该售后申请已处理，无法重复操作");
            }

            // 查找原始订单以获取订单金额
            Order order = orderMapper.selectByOrderSn(orderNumber);
            if (order == null) {
                return Result.error("关联的订单不存在");
            }

            // 根据售后类型处理退款
            // 1-退货退款, 2-仅退款, 3-换货
            if (afterSales.getAfterSalesType() == 1 || afterSales.getAfterSalesType() == 2) {
                // 退货退款或仅退款：将订单金额退回到采购商钱包
                refundOrderAmountToBuyer(order);
            }

            // 更新状态为已处理
            int result = afterSalesMapper.updateStatus(id, 2);
            if (result > 0) {
                return Result.success("售后申请处理成功");
            } else {
                return Result.error("处理售后申请失败");
            }
        } catch (Exception e) {
            return Result.error("处理售后申请失败: " + e.getMessage());
        }
    }

    // 更新方法：将订单金额退还给采购商
    private void refundOrderAmountToBuyer(Order order) {
        try {
            // 向采购商钱包退还金额 - 这会记录为类型3（售后退款）
            walletService.refund(order.getTotalAmount(), order.getBuyerId(), order.getId(), 
                "售后退款 - 订单号: " + order.getOrderSn());
            
            log.info("订单金额 {} 已退还给采购商 {}", order.getTotalAmount(), order.getBuyerId());
        } catch (Exception e) {
            log.error("订单金额退还给采购商失败，订单ID: {}, 采购商ID: {}", order.getId(), order.getBuyerId(), e);
        }
    }

    @Override
    public Result cancelAfterSales(Long id, String username) {
        try {
            // 获取用户信息
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 检查用户角色是否为采购商
            if (user.getUserType() != 1) { // 1代表采购商
                return Result.error("只有采购商才能取消售后申请");
            }

            // 查询售后记录
            AfterSales afterSales = afterSalesMapper.selectById(id);
            if (afterSales == null) {
                return Result.error("售后申请不存在");
            }

            // 检查状态是否为待处理
            if (afterSales.getStatus() != 1) {
                return Result.error("只能取消待处理状态的售后申请");
            }

            // 删除售后申请
            int result = afterSalesMapper.deleteById(id);
            if (result > 0) {
                return Result.success("售后申请取消成功");
            } else {
                return Result.error("取消售后申请失败");
            }
        } catch (Exception e) {
            return Result.error("取消售后申请失败: " + e.getMessage());
        }
    }

    @Override
    public Result getAfterSalesDetail(Long id, String username) {
        try {
            // 获取用户信息
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            AfterSales afterSales = afterSalesMapper.selectById(id);
            if (afterSales == null) {
                return Result.error("售后申请不存在");
            }

            // 验证权限 - 检查用户是否有权查看此售后信息
            boolean hasPermission = false;

            if (user.getUserType() == 1) { // 采购商
                // 采购商可以查看与自己相关的售后
                hasPermission = true; // 简化处理
            } else if (user.getUserType() == 2) { // 供应商
                // 供应商可以查看分配给自己的售后
                hasPermission = user.getUserName().equals(afterSales.getComplainedSupplier());
            }

            if (!hasPermission) {
                return Result.error("无权查看此售后申请");
            }

            return Result.success(afterSales);
        } catch (Exception e) {
            return Result.error("获取售后申请详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result getPendingAfterSalesForSupplier(Long supplierId) {  // 修改参数类型为Long
        try {
            List<AfterSales> afterSalesList = afterSalesMapper.selectBySupplierId(supplierId);
            return Result.success(afterSalesList);
        } catch (Exception e) {
            return Result.error("获取供应商待处理售后申请失败: " + e.getMessage());
        }
    }



    @Override
    public Result getAfterSalesForBuyer(Long buyerId) {
        try {
            List<AfterSales> afterSalesList = afterSalesMapper.selectByBuyerId(buyerId);
            return Result.success(afterSalesList);
        } catch (Exception e) {
            return Result.error("获取采购商售后申请失败: " + e.getMessage());
        }
    }
}