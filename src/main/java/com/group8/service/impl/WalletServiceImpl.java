package com.group8.service.impl;

import com.group8.entity.Wallet;
import com.group8.entity.TransactionRecord;
import com.group8.mapper.WalletMapper;
import com.group8.mapper.TransactionRecordMapper;
import com.group8.service.WalletService;
import com.group8.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;

/**
 * 钱包服务实现类
 */
@Service
public class WalletServiceImpl implements WalletService {
    
    @Autowired
    private WalletMapper walletMapper;
    
    @Autowired
    private TransactionRecordMapper transactionRecordMapper;
    
    @Override
    public Wallet getByUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        Wallet wallet = walletMapper.getByUserId(userId);
        if (wallet == null) {
            // 为用户创建钱包
            wallet = createWallet(userId);
        }
        return wallet;
    }
    
    @Override
    public Wallet createWallet(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        // 检查用户是否已存在钱包
        Wallet existingWallet = walletMapper.getByUserId(userId);
        if (existingWallet != null) {
            return existingWallet;
        }
        
        // 创建新钱包
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setStatus(1); // 1-正常
        
        int result = walletMapper.create(wallet);
        if (result != 1) {
            throw new BusinessException("创建钱包失败");
        }
        
        return wallet;
    }
    
    @Override
    @Transactional
    public TransactionRecord recharge(Integer userId, Double amount, String paymentMethod, String remark) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new BusinessException("支付方式不能为空");
        }
        
        // 获取用户钱包
        Wallet wallet = getByUserId(userId);
        if (wallet.getStatus() != 1) {
            throw new BusinessException("钱包已冻结，无法进行充值操作");
        }
        
        // 更新钱包余额
        int result = walletMapper.updateBalance(wallet.getId(), amount);
        if (result != 1) {
            throw new BusinessException("充值失败");
        }
        
        // 创建交易记录
        TransactionRecord record = new TransactionRecord();
        record.setWalletId(wallet.getId());
        record.setType(1); // 1-充值
        record.setAmount(amount);
        record.setBalanceAfter(wallet.getBalance() + amount);
        record.setPaymentMethod(paymentMethod);
        record.setRemark(remark);
        
        result = transactionRecordMapper.create(record);
        if (result != 1) {
            throw new BusinessException("创建交易记录失败");
        }
        
        return record;
    }
    
    @Override
    @Transactional
    public TransactionRecord pay(Integer userId, Double amount, Integer payeeId, String remark) {
        if (userId == null) {
            throw new BusinessException("付款方用户ID不能为空");
        }
        if (payeeId == null) {
            throw new BusinessException("收款方ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }
        if (userId.equals(payeeId)) {
            throw new BusinessException("不能给自己付款");
        }
        
        // 获取付款方钱包
        Wallet payerWallet = getByUserId(userId);
        if (payerWallet.getStatus() != 1) {
            throw new BusinessException("付款方钱包已冻结，无法进行支付操作");
        }
        
        // 检查付款方余额是否充足
        if (payerWallet.getBalance() < amount) {
            throw new BusinessException("余额不足");
        }
        
        // 获取收款方钱包
        Wallet payeeWallet = getByUserId(payeeId);
        if (payeeWallet.getStatus() != 1) {
            throw new BusinessException("收款方钱包已冻结，无法进行收款操作");
        }
        
        // 付款方扣款
        int result = walletMapper.updateBalance(payerWallet.getId(), -amount);
        if (result != 1) {
            throw new BusinessException("支付失败");
        }
        
        // 收款方收款
        result = walletMapper.updateBalance(payeeWallet.getId(), amount);
        if (result != 1) {
            throw new BusinessException("支付失败");
        }
        
        // 创建付款方交易记录
        TransactionRecord payerRecord = new TransactionRecord();
        payerRecord.setWalletId(payerWallet.getId());
        payerRecord.setType(3); // 3-支付
        payerRecord.setAmount(amount);
        payerRecord.setBalanceAfter(payerWallet.getBalance() - amount);
        payerRecord.setPayeeId(payeeId);
        payerRecord.setRemark(remark);
        
        result = transactionRecordMapper.create(payerRecord);
        if (result != 1) {
            throw new BusinessException("创建交易记录失败");
        }
        
        return payerRecord;
    }
    
    @Override
    public List<TransactionRecord> getTransactionRecords(Integer userId, Map<String, Object> params) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 获取用户钱包
        Wallet wallet = getByUserId(userId);
        
        // 查询交易记录
        return transactionRecordMapper.getByWalletId(wallet.getId(), params);
    }
    
    @Override
    public int getTransactionRecordsCount(Integer userId, Map<String, Object> params) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 获取用户钱包
        Wallet wallet = getByUserId(userId);
        
        // 查询交易记录总数
        return transactionRecordMapper.countByWalletId(wallet.getId(), params);
    }
}