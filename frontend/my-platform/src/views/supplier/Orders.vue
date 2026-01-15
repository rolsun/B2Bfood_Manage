<template>
  <div class="supplier-orders-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-box">
          <span class="title">发货任务管理</span>
          <div class="status-selector">
            <el-radio-group v-model="queryParams.status" size="default" @change="fetchOrders">
              <el-radio-button :label="2">待发货</el-radio-button>
              <el-radio-button :label="4">已发货</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <el-table :data="orderList" v-loading="loading" stripe border>
        <el-table-column prop="orderSn" label="订单编号" width="200" />
        <el-table-column prop="buyerName" label="采购商" />
        <el-table-column label="订单金额" width="120">
          <template #default="scope">
            <b style="color: #f56c6c;">￥{{ scope.row.totalAmount }}</b>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.orderStatus === 2" 
              type="primary" 
              size="small" 
              @click="openShipDialog(scope.row)"
            >立即发货</el-button>
            <el-button v-else type="info" size="small" plain @click="viewShipping(scope.row)">配送详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipVisible" title="物流配送录入" width="450px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司">
          <el-select v-model="shipForm.shippingCompany" placeholder="请选择快递公司" style="width: 100%">
            <el-option label="顺丰冷链" value="顺丰冷链" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="自有配送" value="自有配送" />
          </el-select>
        </el-form-item>
        <el-form-item label="运单号">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入运单号" />
        </el-form-item>
        <el-form-item label="预计送达">
          <el-date-picker
            v-model="shipForm.estimatedDeliveryTime"
            type="datetime"
            placeholder="选择预计送达时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="submitShip" :loading="shipLoading">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const shipLoading = ref(false);
const orderList = ref([]);
const shipVisible = ref(false);
const currentOrderId = ref(null);

const queryParams = reactive({ status: 2, page: 1, limit: 20 });
const shipForm = reactive({
  shippingCompany: '顺丰冷链',
  trackingNumber: '',
  estimatedDeliveryTime: ''
});

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await request.get('/orders/supplier/pending', { params: queryParams });
    if (res.code === 1) {
      orderList.value = res.data.list || res.data || [];
    }
  } finally {
    loading.value = false;
  }
};

const openShipDialog = (row) => {
  currentOrderId.value = row.id || row.orderId;
  shipForm.trackingNumber = '';
  shipVisible.value = true;
};

const submitShip = async () => {
  if (!shipForm.trackingNumber) return ElMessage.warning('请输入运单号');
  shipLoading.value = true;
  try {
    const res = await request.post(`/orders/${currentOrderId.value}/delivery`, shipForm);
    if (res.code === 1) {
      ElMessage.success('发货信息已同步');
      shipVisible.value = false;
      fetchOrders();
    }
  } finally {
    shipLoading.value = false;
  }
};

const viewShipping = (row) => {
  ElMessage.info(`快递：${row.shippingCompany || '顺丰'}，单号：${row.trackingNumber || '未录入'}`);
};

onMounted(fetchOrders);
</script>

<style scoped>
.header-box { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 18px; font-weight: bold; }
</style>