package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 售后操作日志实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSalesLog {
    /**
     * 日志ID
     */
    private Integer id;
    
    /**
     * 售后申请ID
     */
    private Integer afterSalesId;
    
    /**
     * 操作人ID
     */
    private Integer operatorId;
    
    /**
     * 操作人名称
     */
    private String operatorName;
    
    /**
     * 操作内容
     */
    private String operationContent;
    
    /**
     * 操作时间
     */
    private Date operationTime;
}