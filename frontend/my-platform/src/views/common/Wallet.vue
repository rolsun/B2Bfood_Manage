<template>
  <div class="wallet-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="balance-card" shadow="hover">
          <div class="label">我的可用余额 (CNY)</div>
          <!-- 统一使用 walletInfo.balance -->
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
        <!-- 字段：createTime -->
        <el-table-column prop="createTime" label="交易时间" width="180" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="交易类型" width="130" align="center">
          <template #default="scope">
            <!-- 1-充值, 2-用户支付, 3-售后退款, 4-提现 -->
            <el-tag :type="getTypeTag(scope.row.transactionType)" effect="light" round>
              {{ getTypeText(scope.row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="变动金额" width="150" align="center">
          <template #default="scope">
            <!-- 核心逻辑：根据 userType 和 transactionType 自动计算正负号 -->
            <b :style="{ color: getAmountColor(scope.row), fontSize: '16px' }">
              {{ getAmountSign(scope.row) }}{{ scope.row.amount }}
            </b>
          </template>
        </el-table-column>

        <!-- 字段：afterBalance -->
        <el-table-column prop="afterBalance" label="交易后余额" width="150" align="center">
          <template #default="scope">
            <span style="color: #606266; font-weight: 500;">￥{{ scope.row.afterBalance }}</span>
          </template>
        </el-table-column>

        <!-- 字段：description 和 orderId -->
        <el-table-column label="备注/关联单号" min-width="200">
          <template #default="scope">
            <div style="font-size: 13px;">
              <div v-if="scope.row.orderId" style="color: #409EFF; margin-bottom: 2px;">单号: {{ scope.row.orderId }}</div>
              <div style="color: #909399;">{{ scope.row.description || '钱包账务变动' }}</div>
            </div>
          </template>
        </el-table-column>
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
import { useUserStore } from '../../store/user';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';

const userStore = useUserStore();
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

// 时间格式化
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return timeStr.replace('T', ' ');
};

/**
 * 【核心逻辑 1】：判断正负号符号
 * 逻辑：
 * - 1(充值): 均为 +
 * - 2(支付): 采购商 - , 供应商 +
 * - 3(退款): 采购商 + , 供应商 -
 */
const getAmountSign = (row) => {
  const type = Number(row.transactionType);
  const role = Number(userStore.userType);

  if (type === 1) return '+'; // 充值永远是加
  if (type === 4) return '-'; // 提现永远是减

  if (role === 1) { // 采购商角色
    return type === 3 ? '+' : '-'; // 售后退款是+, 支付是-
  } else { // 供应商角色
    return type === 2 ? '+' : '-'; // 用户支付(收入)是+, 售后退款(支出)是-
  }
};

// 【核心逻辑 2】：判断颜色
const getAmountColor = (row) => {
  return getAmountSign(row) === '+' ? '#67C23A' : '#F56C6C';
};

// 翻译类型文字
const getTypeText = (type) => {
  const map = { 1: '资金充值', 2: '用户支付', 3: '售后退款', 4: '余额提现' };
  return map[Number(type)] || '系统流水';
};

// 翻译标签颜色
const getTypeTag = (type) => {
  const t = Number(type);
  if (t === 1 || (t === 2 && userStore.userType == 2) || (t === 3 && userStore.userType == 1)) {
    return 'success'; // 收入项绿色
  }
  return 'danger'; // 支出项红色
};

const initData = async () => {
  loading.value = true;
  try {
    const resW = await request.get('/wallet/info');
    if (resW.code === 1) {
      walletInfo.value = resW.data;
    }

    const resT = await request.get('/wallet/transactions', { params: queryParams });
    if (resT.code === 1) {
      // 适配分页 list 结构
      transactions.value = resT.data.list || [];
      total.value = resT.data.total || 0;
    }
  } catch (error) {
    console.error("加载钱包数据失败", error);
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