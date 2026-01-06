package com.group8.mapper;

import com.group8.entity.AfterSalesLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 售后操作日志Mapper接口
 */
@Mapper
public interface AfterSalesLogMapper {
    /**
     * 创建售后操作日志
     * @param log 售后操作日志信息
     * @return 影响行数
     */
    int create(AfterSalesLog log);
    
    /**
     * 根据售后申请ID获取操作日志列表
     * @param afterSalesId 售后申请ID
     * @return 操作日志列表
     */
    List<AfterSalesLog> listByAfterSalesId(@Param("afterSalesId") Integer afterSalesId);
}