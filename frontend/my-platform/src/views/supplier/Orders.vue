<template>
  <el-card>
    <template #header><h2>待处理订单(待发货)</h2></template>
    <el-table :data="pendingOrders">
      <el-table-column prop="orderSn" label="订单号" />
      <el-table-column prop="buyerName" label="采购商" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleShip(scope.row)">填写发货信息</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessageBox } from 'element-plus';

const pendingOrders = ref([]);
const fetchData = async () => {
  const res = await request.get('/orders/supplier/pending', { params: { status: 2 } });
  pendingOrders.value = res.data.list;
};

const handleShip = (row) => {
  ElMessageBox.prompt('请输入物流单号', '确认发货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    await request.post(`/orders/${row.orderId}/delivery`, {
      shippingCompany: '顺丰快递',
      trackingNumber: value
    });
    fetchData();
  });
};

onMounted(fetchData);
</script>