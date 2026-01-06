package com.group8.mapper;

import com.group8.entity.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 交易记录Mapper接口
 */
@Mapper
public interface TransactionRecordMapper {
    /**
     * 创建交易记录
     * @param record 交易记录信息
     * @return 影响行数
     */
    int create(TransactionRecord record);
    
    /**
     * 根据钱包ID获取交易记录列表
     * @param walletId 钱包ID
     * @param params 查询参数
     * @return 交易记录列表
     */
    List<TransactionRecord> getByWalletId(@Param("walletId") Integer walletId, @Param("params") Map<String, Object> params);
    
    /**
     * 根据钱包ID获取交易记录总数
     * @param walletId 钱包ID
     * @param params 查询参数
     * @return 交易记录总数
     */
    int countByWalletId(@Param("walletId") Integer walletId, @Param("params") Map<String, Object> params);
    
    /**
     * 根据ID获取交易记录
     * @param id 交易记录ID
     * @return 交易记录信息
     */
    TransactionRecord getById(@Param("id") Integer id);
}