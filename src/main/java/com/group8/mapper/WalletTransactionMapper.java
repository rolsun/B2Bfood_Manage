package com.group8.mapper;

import com.group8.entity.WalletTransaction;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface WalletTransactionMapper {
    
    @Insert("INSERT INTO wallet_transactions (user_id, transaction_type, amount, before_balance, after_balance, " +
            "order_id, description, status, create_time) VALUES (#{transaction.userId}, #{transaction.transactionType}, " +
            "#{transaction.amount}, #{transaction.beforeBalance}, #{transaction.afterBalance}, #{transaction.orderId}, " +
            "#{transaction.description}, #{transaction.status}, #{transaction.createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "transaction.id")
    int insert(@Param("transaction") WalletTransaction transaction);
    
    List<WalletTransaction> selectTransactions(Map<String, Object> params);
    
    int countTransactions(Map<String, Object> params);
}
