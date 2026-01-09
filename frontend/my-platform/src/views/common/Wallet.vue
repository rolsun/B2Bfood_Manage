<template>
  <div class="wallet-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="balance-card">
          <div class="label">我的可用余额 (CNY)</div>
          <div class="amount">￥ {{ wallet.balance?.toFixed(2) || '0.00' }}</div>
          <div class="btn-group">
            <el-button type="primary" @click="showRecharge = true">充值余额</el-button>
            <el-button type="success" plain @click="handleWithdraw">提现</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px;">
      <template #header><b>最近交易账单</b></template>
      <el-table :data="transactions" stripe>
        <el-table-column prop="transactionTime" label="交易时间" width="180" />
        <el-table-column label="交易类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'">
              {{ scope.row.type === 1 ? '充值' : scope.row.type === 3 ? '订单支付' : '提现' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="变动金额">
          <template #default="scope">
            <b :style="{ color: scope.row.type === 1 ? '#67C23A' : '#F56C6C' }">
              {{ scope.row.type === 1 ? '+' : '-' }}{{ scope.row.amount }}
            </b>
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="变动后余额" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>

    <!-- 充值弹窗 -->
    <el-dialog v-model="showRecharge" title="账户余额充值" width="400px">
      <el-form label-position="top">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeForm.amount" :min="0.01" style="width: 100%" />
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
        <el-button type="primary" style="width: 100%" @click="submitRecharge">立即支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage } from 'element-plus';

const wallet = ref({});
const transactions = ref([]);
const showRecharge = ref(false);
const rechargeForm = reactive({ amount: 100, paymentMethod: 'alipay', remark: '用户在线充值' });

const initData = async () => {
  const resW = await request.get('/wallet/info');
  wallet.value = resW.data;
  const resT = await request.get('/wallet/transactions');
  transactions.value = resT.data.list;
};

const submitRecharge = async () => {
  await request.post('/wallet/recharge', rechargeForm);
  ElMessage.success('充值成功！');
  showRecharge.value = false;
  initData();
};

const handleWithdraw = () => ElMessage.info('功能开发中...');

onMounted(initData);
</script>

<style scoped>
.balance-card { background: #f0f9eb; border: 1px solid #e1f3d8; }
.label { font-size: 14px; color: #67c23a; }
.amount { font-size: 36px; font-weight: bold; margin: 15px 0; color: #333; }
.btn-group { display: flex; gap: 10px; }
</style>