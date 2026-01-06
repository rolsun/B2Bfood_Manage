package com.group8.controller;

import com.group8.entity.Result;
import com.group8.entity.AfterSales;
import com.group8.entity.AfterSalesLog;
import com.group8.service.AfterSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 售后控制器
 */
@RestController
@RequestMapping("/after-sales")
public class AfterSalesController {
    
    @Autowired
    private AfterSalesService afterSalesService;
    
    /**
     * 提交售后申请
     * @param afterSales 售后申请信息
     * @return 售后申请信息
     */
    @PostMapping("/apply")
    public Result<AfterSales> apply(@RequestBody AfterSales afterSales) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        afterSales.setBuyerId(2003);
        // 模拟供应商ID，实际项目中应从订单信息中获取
        afterSales.setSellerId(3001);
        
        AfterSales result = afterSalesService.apply(afterSales);
        return Result.success("售后申请提交成功", result);
    }
    
    /**
     * 获取售后申请列表
     * @param page 页码
     * @param limit 每页条目数
     * @param status 售后状态
     * @param afterSalesType 售后类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 售后申请列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer afterSalesType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("buyerId", userId);
        params.put("status", status);
        params.put("afterSalesType", afterSalesType);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        // 获取售后申请列表
        List<AfterSales> list = afterSalesService.list(params);
        // 获取售后申请总数
        int total = afterSalesService.count(params);
        
        // 构建响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        
        return Result.success(result);
    }
    
    /**
     * 获取售后申请详情
     * @param afterSalesId 售后申请ID
     * @return 售后申请详情
     */
    @GetMapping("/{afterSalesId}")
    public Result<AfterSales> getById(@PathVariable Integer afterSalesId) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        AfterSales afterSales = afterSalesService.getById(afterSalesId, userId);
        return Result.success(afterSales);
    }
    
    /**
     * 取消售后申请
     * @param afterSalesId 售后申请ID
     * @return 售后申请信息
     */
    @DeleteMapping("/{afterSalesId}")
    public Result<AfterSales> cancel(@PathVariable Integer afterSalesId) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        AfterSales afterSales = afterSalesService.cancel(afterSalesId, userId);
        return Result.success("售后申请已取消", afterSales);
    }
    
    /**
     * 供应商获取待审核的售后申请列表
     * @param page 页码
     * @param limit 每页条目数
     * @param afterSalesType 售后类型
     * @return 售后申请列表
     */
    @GetMapping("/supplier/pending")
    public Result<Map<String, Object>> getPendingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer afterSalesType) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 3001;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", userId);
        params.put("afterSalesType", afterSalesType);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        // 获取待审核售后申请列表
        List<AfterSales> list = afterSalesService.getPendingList(params);
        // 获取待审核售后申请总数
        int total = afterSalesService.countPending(params);
        
        // 构建响应数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        
        return Result.success(result);
    }
    
    /**
     * 供应商审核售后申请
     * @param afterSalesId 售后申请ID
     * @param params 审核参数
     * @return 售后申请信息
     */
    @PostMapping("/{afterSalesId}/review")
    public Result<AfterSales> review(@PathVariable Integer afterSalesId, @RequestBody Map<String, Object> params) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 3001;
        
        Integer reviewResult = (Integer) params.get("reviewResult");
        String reviewComment = (String) params.get("reviewComment");
        Double actualRefundAmount = (Double) params.get("refundAmount");
        
        AfterSales afterSales = afterSalesService.review(afterSalesId, reviewResult, reviewComment, actualRefundAmount, userId);
        return Result.success("审核完成", afterSales);
    }
    
    /**
     * 采购商上传退货信息
     * @param afterSalesId 售后申请ID
     * @param params 退货信息
     * @return 售后申请信息
     */
    @PostMapping("/{afterSalesId}/return")
    public Result<AfterSales> uploadReturnInfo(@PathVariable Integer afterSalesId, @RequestBody Map<String, Object> params) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        String shippingCompany = (String) params.get("shippingCompany");
        String trackingNumber = (String) params.get("trackingNumber");
        String returnNote = (String) params.get("returnNote");
        
        AfterSales afterSales = afterSalesService.uploadReturnInfo(afterSalesId, shippingCompany, trackingNumber, returnNote, userId);
        return Result.success("退货信息上传成功", afterSales);
    }
    
    /**
     * 供应商确认收货
     * @param afterSalesId 售后申请ID
     * @param params 收货信息
     * @return 售后申请信息
     */
    @PostMapping("/{afterSalesId}/confirm-receive")
    public Result<AfterSales> confirmReceive(@PathVariable Integer afterSalesId, @RequestBody Map<String, Object> params) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 3001;
        
        Integer receiveStatus = (Integer) params.get("receiveStatus");
        String receiveNote = (String) params.get("receiveNote");
        
        AfterSales afterSales = afterSalesService.confirmReceive(afterSalesId, receiveStatus, receiveNote, userId);
        return Result.success("收货确认成功", afterSales);
    }
    
    /**
     * 获取售后操作日志
     * @param afterSalesId 售后申请ID
     * @return 操作日志列表
     */
    @GetMapping("/{afterSalesId}/logs")
    public Result<List<AfterSalesLog>> getLogs(@PathVariable Integer afterSalesId) {
        // 模拟当前登录用户ID，实际项目中应从Token中获取
        Integer userId = 2003;
        
        // 验证用户权限，实际项目中应在Service层实现
        afterSalesService.getById(afterSalesId, userId);
        
        List<AfterSalesLog> logs = afterSalesService.getLogs(afterSalesId);
        return Result.success(logs);
    }
}