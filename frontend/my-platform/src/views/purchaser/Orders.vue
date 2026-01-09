<template>
  <div class="orders-container">
    <el-card shadow="hover">
      <template #header>
        <div class="header-flex">
          <span class="title">我的采购订单记录</span>
          <el-button type="primary" size="small" icon="Refresh" @click="fetchOrders">刷新列表</el-button>
        </div>
      </template>

      <!-- 订单列表表格 -->
      <el-table :data="orderList" v-loading="loading" stripe border style="width: 100%">
        <!-- 这里的 prop 改为 id，因为你确认后端返回的是 id -->
        <el-table-column prop="id" label="订单ID" width="80" align="center" />
        
        <el-table-column prop="orderSn" label="订单编号" min-width="200" align="center" />
        
        <el-table-column label="订单金额" width="120">
          <template #default="scope">
            <span class="price-tag">￥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="下单时间" width="180" align="center" />

        <el-table-column label="当前状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.orderStatus)">
              {{ getStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="管理操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <!-- 状态为 1 (待支付) 时显示 -->
            <template v-if="scope.row.orderStatus === 1">
              <el-button type="warning" size="small" icon="CreditCard" @click="handlePay(scope.row)">立即支付</el-button>
              <el-button type="danger" size="small" plain @click="handleCancel(scope.row)">取消</el-button>
            </template>
            
            <el-button v-else type="primary" size="small" plain @click="showDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-box">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="queryParam.limit"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详细信息" width="500px">
      <div v-if="currentOrder" class="detail-content">
        <p><b>订单ID：</b>{{ currentOrder.id }}</p>
        <p><b>收货信息：</b>{{ currentOrder.deliveryAddress?.contactName }} ({{ currentOrder.deliveryAddress?.contactPhone }})</p>
        <p><b>收货地址：</b>{{ currentOrder.deliveryAddress?.address }}</p>
        <p><b>供应商：</b>{{ currentOrder.supplierName }}</p>
        <el-divider />
        <p v-if="currentOrder.cancelReason" style="color: #F56C6C;"><b>取消原因：</b>{{ currentOrder.cancelReason }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh, CreditCard } from '@element-plus/icons-vue';

const loading = ref(false);
const orderList = ref([]);
const total = ref(0);
const detailVisible = ref(false);
const currentOrder = ref(null);

const queryParam = reactive({ page: 1, limit: 10 });

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await request.get('/orders', { params: queryParam });
    if (res.code === 1) {
      // 适配分页结构
      if (res.data.list) {
        orderList.value = res.data.list;
        total.value = res.data.total;
      } else {
        orderList.value = res.data;
        total.value = res.data.length;
      }
    }
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (p) => {
  queryParam.page = p;
  fetchOrders();
};

/**
 * 核心修复点：将所有接口调用的参数由 orderId 改为 id
 */

// 1. 支付订单：使用 row.id
const handlePay = (row) => {
  const targetId = row.id; // 确定使用 id 字段
  
  if (!targetId) {
    ElMessage.error("未找到有效的订单ID");
    return;
  }

  ElMessageBox.confirm(`确认支付订单吗？金额：￥${row.totalAmount}`, '支付确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      // 发送请求到 /orders/pay/{id}
      const res = await request.post(`/orders/pay/${targetId}`);
      if (res.code === 1) {
        ElMessage.success('支付成功！');
        fetchOrders(); // 刷新列表查看状态变更
      }
    } catch (e) {
      console.error("支付失败", e);
    }
  });
};

// 2. 取消订单：使用 row.id
const handleCancel = (row) => {
  const targetId = row.id;
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '原因不能为空'
  }).then(async ({ value }) => {
    try {
      // 发送请求到 /orders/{id}/cancel
      const res = await request.post(`/orders/${targetId}/cancel`, { cancelReason: value });
      if (res.code === 1) {
        ElMessage.info('订单已取消');
        fetchOrders();
      }
    } catch (e) {}
  });
};

// 3. 查看详情：使用 row.id
const showDetail = async (row) => {
  const targetId = row.id;
  try {
    // 发送请求到 /orders/{id}
    const res = await request.get(`/orders/${targetId}`);
    if (res.code === 1) {
      currentOrder.value = res.data;
      detailVisible.value = true;
    }
  } catch (e) {}
};

const getStatusText = (s) => {
  const map = { 1: '待支付', 2: '待接单', 3: '待发货', 4: '已发货', 5: '已完成', 6: '已取消' };
  return map[s] || '未知';
};
const getStatusType = (s) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'info', 4: 'primary', 5: 'success', 6: 'info' };
  return map[s] || 'info';
};

onMounted(fetchOrders);
</script>

<style scoped>
.header-flex { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 18px; font-weight: bold; color: #409EFF; }
.price-tag { color: #f56c6c; font-weight: bold; }
.pagination-box { margin-top: 20px; display: flex; justify-content: flex-end; }
.detail-content p { line-height: 2; font-size: 14px; }
</style>