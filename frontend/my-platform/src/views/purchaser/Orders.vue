<template>
  <div class="orders-page">
    <!-- 顶部标题区 -->
    <div class="page-header">
      <div class="header-left">
        <h1>采购订单管理</h1>
        <p>在此追踪您的食材采购进度、支付待处理订单并查看物流状态</p>
      </div>
      <el-button type="primary" icon="Refresh" circle @click="fetchOrders" />
    </div>

    <!-- 核心订单列表卡片 -->
    <el-card shadow="never" class="list-card">
      <!-- 状态筛选：对应后端 1-待支付, 2-待发货, 3-已发货 -->
      <el-tabs v-model="activeTab" class="custom-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="0" />
        <el-tab-pane label="待支付" name="1" />
        <el-tab-pane label="待发货" name="2" />
        <el-tab-pane label="已发货" name="3" />
      </el-tabs>

      <div class="table-container" v-loading="loading">
        <el-table :data="orderList" class="modern-table" border stripe>
          <el-table-column label="订单流水号" min-width="220">
            <template #default="scope">
              <div class="order-sn"># {{ scope.row.orderSn }}</div>
              <div class="order-time">{{ scope.row.createTime }}</div>
            </template>
          </el-table-column>

          <el-table-column label="总计金额" width="150">
            <template #default="scope">
              <span class="price-text">￥{{ scope.row.totalAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="订单状态" width="120" align="center">
            <template #default="scope">
              <!-- 逻辑：1-红色，2-橙色，3-绿色 -->
              <el-tag :type="getStatusTag(scope.row.orderStatus)" round effect="light">
                {{ getStatusText(scope.row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="管理操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <!-- 仅在状态为 1 (待支付) 时显示支付按钮 -->
              <template v-if="scope.row.orderStatus === 1">
                <el-button type="primary" size="small" round @click="handlePay(scope.row)">立即支付</el-button>
                <el-button type="danger" size="small" link @click="handleCancel(scope.row)">取消</el-button>
              </template>
              
              <!-- 状态为 2 或 3 时仅查看详情 -->
              <el-button v-else type="info" size="small" plain round @click="viewDetail(scope.row)">详情明细</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <div class="pagination-area">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.limit"
            :total="total"
            layout="total, prev, pager, next"
            background
            @current-change="fetchOrders"
          />
        </div>
      </div>
    </el-card>

    <!-- 订单明细弹窗 -->
    <el-dialog v-model="detailVisible" title="采购订单详情" width="600px" destroy-on-close>
      <div v-if="currentOrder" class="order-detail-content">
        <div class="detail-header">
          <span>订单状态：<b :style="{color: getStatusColor(currentOrder.orderStatus)}">{{ getStatusText(currentOrder.orderStatus) }}</b></span>
          <span>供货商：{{ currentOrder.supplierName || '默认仓库' }}</span>
        </div>

        <!-- 具体的商品明细表格 -->
        <el-table :data="currentOrder.items" size="small" border style="margin-top: 20px;">
          <el-table-column prop="productName" label="食材名称" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="scope">￥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>

        <div class="logistics-box" v-if="currentOrder.orderStatus === 3">
          <p><b>配送信息</b></p>
          <div class="log-item">物流公司：{{ currentOrder.shippingCompany }}</div>
          <div class="log-item">运单编号：{{ currentOrder.trackingNumber }}</div>
        </div>

        <div class="total-bar">
          实付总金额：<span class="price-val">￥ {{ currentOrder.totalAmount }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';

const loading = ref(false);
const orderList = ref([]);
const total = ref(0);
const activeTab = ref('0'); // 默认全部
const detailVisible = ref(false);
const currentOrder = ref(null);

const queryParams = reactive({ page: 1, limit: 10 });

// 1. 获取订单列表
const fetchOrders = async () => {
  loading.value = true;
  try {
    const params = { ...queryParams };
    // 如果不是“全部”，则传递状态码进行筛选
    if (activeTab.value !== '0') {
      params.status = Number(activeTab.value);
    }
    
    const res = await request.get('/orders', { params });
    if (res.code === 1) {
      orderList.value = res.data.list || res.data || [];
      total.value = res.data.total || orderList.value.length;
    }
  } finally {
    loading.value = false;
  }
};

const handleTabChange = () => {
  queryParams.page = 1;
  fetchOrders();
};

// 2. 支付订单：POST /orders/pay/{id}
const handlePay = (row) => {
  ElMessageBox.confirm(`确认支付此订单吗？金额：￥${row.totalAmount}`, '账户结算', {
    confirmButtonText: '立即支付',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    const res = await request.post(`/orders/pay/${row.id}`);
    if (res.code === 1) {
      ElMessage.success('支付成功，请等待供应商发货');
      fetchOrders();
    }
  });
};

// 3. 取消订单
const handleCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消订单').then(async ({ value }) => {
    const res = await request.post(`/orders/${row.id}/cancel`, { cancelReason: value });
    if (res.code === 1) {
      ElMessage.info('订单已取消');
      fetchOrders();
    }
  });
};

// 4. 查看详情：GET /orders/{id}
const viewDetail = async (row) => {
  detailVisible.value = true;
  const res = await request.get(`/orders/${row.id}`);
  if (res.code === 1) {
    currentOrder.value = res.data;
  }
};

// 状态码转换 (对齐后端 1, 2, 3)
const getStatusText = (s) => ({ 1: '待支付', 2: '待发货', 3: '已发货' }[s] || '未知');
const getStatusTag = (s) => ({ 1: 'danger', 2: 'warning', 3: 'success' }[s] || 'info');
const getStatusColor = (s) => ({ 1: '#f56c6c', 2: '#e6a23c', 3: '#67c23a' }[s] || '#909399');

onMounted(fetchOrders);
</script>

<style scoped>
.orders-page { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 25px; }
.header-left h1 { margin: 0; font-size: 26px; font-weight: 800; color: #1e293b; }
.header-left p { color: #64748b; margin-top: 6px; font-size: 14px; }

.list-card { border-radius: 20px !important; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.05) !important; }
.custom-tabs { margin-bottom: 15px; }

.modern-table :deep(.el-table__header) th { background: #f8fafc; color: #475569; font-weight: 700; height: 50px; }
.order-sn { font-weight: 600; color: #334155; font-size: 14px; }
.order-time { font-size: 12px; color: #94a3b8; margin-top: 4px; }
.price-text { color: #ef4444; font-weight: 800; font-size: 16px; }

.pagination-area { margin-top: 30px; display: flex; justify-content: center; }

/* 弹窗明细样式 */
.detail-header { display: flex; justify-content: space-between; background: #f8fafc; padding: 15px; border-radius: 12px; font-size: 14px; }
.logistics-box { margin-top: 20px; background: #f0f9eb; padding: 15px; border-radius: 12px; color: #67c23a; font-size: 13px; line-height: 2; }
.total-bar { margin-top: 25px; text-align: right; font-weight: 800; font-size: 16px; }
.price-val { color: #ef4444; font-size: 24px; margin-left: 10px; }
</style>