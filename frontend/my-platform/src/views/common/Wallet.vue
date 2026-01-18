<template>
  <div class="wallet-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="balance-card" shadow="hover">
          <div class="label">我的可用余额 (CNY)</div>
          <!-- 核心修复：变量名由 wallet 改为 walletInfo 以匹配脚本 -->
          <div class="amount">￥ {{ walletInfo.balance?.toFixed(2) || '0.00' }}</div>
          <div class="btn-group">
            <el-button type="primary" size="large" @click="showRecharge = true" style="width: 100%; border-radius: 8px; font-weight: bold;">
              充值余额
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 25px; border-radius: 12px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <b style="font-size: 16px; color: #303133;">最近交易账单</b>
          <el-button link type="primary" icon="Refresh" @click="initData">刷新账单</el-button>
        </div>
      </template>

      <el-table :data="transactions" stripe v-loading="loading">
        <!-- 字段对齐：后端 createTime -->
        <el-table-column prop="createTime" label="交易时间" width="180" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="交易类型" width="120" align="center">
          <template #default="scope">
            <!-- 逻辑修复：Number转换确保匹配，1-充值显示成功色，3-支出显示危险色 -->
            <el-tag :type="isRecharge(scope.row.transactionType) ? 'success' : 'danger'" effect="light" round>
              {{ isRecharge(scope.row.transactionType) ? '账户充值' : '订单支付' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="变动金额" width="150" align="center">
          <template #default="scope">
            <!-- 逻辑修复：强制正负号显示 -->
            <b :style="{ color: isRecharge(scope.row.transactionType) ? '#67C23A' : '#F56C6C', fontSize: '16px' }">
              {{ isRecharge(scope.row.transactionType) ? '+' : '-' }}{{ scope.row.amount }}
            </b>
          </template>
        </el-table-column>

        <!-- 字段对齐：后端 afterBalance -->
        <el-table-column prop="afterBalance" label="交易后余额" width="150" align="center">
          <template #default="scope">
            <span style="color: #606266; font-weight: 500;">￥{{ scope.row.afterBalance }}</span>
          </template>
        </el-table-column>

        <!-- 字段对齐：后端 description -->
        <el-table-column prop="description" label="备注" show-overflow-tooltip />
      </el-table>

      <!-- 分页功能 -->
      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="queryParams.page"
          :total="total"
          :page-size="queryParams.limit"
          background
          layout="total, prev, pager, next"
          @current-change="initData"
        />
      </div>
    </el-card>

    <!-- 充值弹窗 -->
    <el-dialog v-model="showRecharge" title="余额充值" width="400px" destroy-on-close>
      <el-form :model="rechargeForm" label-position="top">
        <el-form-item label="充值金额 (元)">
          <el-input-number v-model="rechargeForm.amount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="rechargeForm.paymentMethod" style="width: 100%">
            <el-option label="支付宝" value="alipay" />
            <el-option label="微信支付" value="wechat" />
            <el-option label="网银转账" value="bank_card" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRecharge = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge" :loading="submitLoading">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';

// 核心修复：变量名改为 walletInfo 匹配模板
const walletInfo = ref({});
const transactions = ref([]);
const loading = ref(false);
const showRecharge = ref(false);
const submitLoading = ref(false);
const total = ref(0);

const queryParams = reactive({
  page: 1,
  limit: 10
});

const rechargeForm = reactive({ 
  amount: 100, 
  paymentMethod: 'alipay', 
  remark: '钱包充值' 
});

// 类型判断逻辑：1为充值，其他为支出
const isRecharge = (type) => {
  return Number(type) === 1;
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return timeStr.replace('T', ' ');
};

const initData = async () => {
  loading.value = true;
  try {
    // 1. 获取余额
    const resW = await request.get('/wallet/info');
    if (resW.code === 1) {
      walletInfo.value = resW.data;
    }

    // 2. 获取交易记录
    const resT = await request.get('/wallet/transactions', { params: queryParams });
    if (resT.code === 1) {
      // 适配分页结构
      transactions.value = resT.data.list || [];
      total.value = resT.data.total || 0;
    }
  } catch (error) {
    console.error("加载失败", error);
  } finally {
    loading.value = false;
  }
};

const submitRecharge = async () => {
  submitLoading.value = true;
  try {
    const res = await request.post('/wallet/recharge', rechargeForm);
    if (res.code === 1) {
      ElMessage.success('充值成功！');
      showRecharge.value = false;
      await initData(); 
    }
  } finally {
    submitLoading.value = false;
  }
};

onMounted(initData);
</script>

<style scoped>
.wallet-page { max-width: 1200px; margin: 0 auto; }
.balance-card { background: #f0f9eb; border: 1px solid #e1f3d8; border-radius: 12px; }
.label { font-size: 14px; color: #67c23a; margin-bottom: 8px; }
.amount { font-size: 36px; font-weight: bold; margin-bottom: 20px; color: #303133; }
:deep(.el-table__header) th { background-color: #f8fafc !important; color: #606266; font-weight: bold; }
</style>