package com.group8.mapper;

import com.group8.entity.Wallet;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;

@Mapper
public interface WalletMapper {
    
    @Insert("INSERT INTO wallets (user_id, balance, total_recharge, total_spent, create_time, update_time) " +
            "VALUES (#{wallet.userId}, #{wallet.balance}, #{wallet.totalRecharge}, " +
            "#{wallet.totalSpent}, #{wallet.createTime}, #{wallet.updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "wallet.id")
    int insert(@Param("wallet") Wallet wallet);
    
    @Select("SELECT * FROM wallets WHERE user_id = #{userId}")
    Wallet selectByUserId(@Param("userId") Long userId);
    
    @Update("UPDATE wallets SET balance = balance + #{amount}, total_recharge = total_recharge + #{amount}, " +
            "update_time = NOW() WHERE user_id = #{userId}")
    int recharge(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    @Update("UPDATE wallets SET balance = balance - #{amount}, total_spent = total_spent + #{amount}, " +
            "update_time = NOW() WHERE user_id = #{userId} AND balance >= #{amount}")
    int pay(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    @Update("UPDATE wallets SET balance = balance - #{amount}, update_time = NOW() WHERE user_id = #{userId} AND balance >= #{amount}")
    int withdraw(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    @Update("UPDATE wallets SET balance = #{newBalance}, update_time = NOW() WHERE user_id = #{userId}")
    int updateBalance(@Param("userId") Long userId, @Param("newBalance") BigDecimal newBalance);
    
    // 新增方法：增加余额（用于订单收入等场景）
    @Update("UPDATE wallets SET balance = balance + #{amount}, total_recharge = total_recharge + #{amount}, " +
            "update_time = NOW() WHERE user_id = #{userId}")
    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    // 新增方法：减少余额（用于退款等场景）
    @Update("UPDATE wallets SET balance = balance - #{amount}, total_spent = total_spent - #{amount}, " +
            "update_time = NOW() WHERE user_id = #{userId} AND balance >= #{amount}")
    int decreaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}