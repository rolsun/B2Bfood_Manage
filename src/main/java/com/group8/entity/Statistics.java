package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据统计实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    /**
     * 统计类型
     */
    private String type;
    
    /**
     * 统计值
     */
    private Object value;
    
    /**
     * 统计时间
     */
    private String time;
}