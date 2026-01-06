package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 钱包实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    /**
     * 钱包ID
     */
    private Integer id;
    
    /**
     * 所属用户ID
     */
    private Integer userId;
    
    /**
     * 当前余额(元)
     */
    private Double balance;
    
    /**
     * 钱包状态: 1-正常, 2-冻结
     */
    private Integer status;
    
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
}