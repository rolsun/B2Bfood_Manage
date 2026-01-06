package com.group8.mapper;

import com.group8.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 钱包Mapper接口
 */
@Mapper
public interface WalletMapper {
    /**
     * 根据用户ID获取钱包信息
     * @param userId 用户ID
     * @return 钱包信息
     */
    Wallet getByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据钱包ID获取钱包信息
     * @param id 钱包ID
     * @return 钱包信息
     */
    Wallet getById(@Param("id") Integer id);
    
    /**
     * 创建钱包
     * @param wallet 钱包信息
     * @return 影响行数
     */
    int create(Wallet wallet);
    
    /**
     * 更新钱包余额
     * @param id 钱包ID
     * @param amount 变动金额
     * @return 影响行数
     */
    int updateBalance(@Param("id") Integer id, @Param("amount") Double amount);
    
    /**
     * 更新钱包状态
     * @param id 钱包ID
     * @param status 钱包状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}