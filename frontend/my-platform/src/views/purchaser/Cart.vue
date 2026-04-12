<template>
  <div class="cart-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-box">
          <span class="title">🛒 我的采购清单 (购物车)</span>
          <el-button type="primary" size="small" plain @click="fetchCart">刷新数据</el-button>
        </div>
      </template>

      <!-- 购物车表格 -->
      <el-table :data="cartList" v-loading="loading" stripe border>
        <!-- 显示商品ID（来自 products 表） -->
        <el-table-column prop="productId" label="食材ID" width="80" align="center" />
        
        <!-- 食材名称（需要你按上一步修改后端后才能显示） -->
        <el-table-column label="食材名称" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.productName || '未命名商品' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="单价" width="100">
          <template #default="scope">
            ￥{{ (scope.row.price || 0).toFixed(2) }}
          </template>
        </el-table-column>

        <el-table-column label="采购数量" width="160" align="center">
          <template #default="scope">
            <el-input-number 
              v-model="scope.row.quantity" 
              :min="1" 
              size="small" 
              @change="(val) => handleUpdateQuantity(scope.row, val)"
            />
          </template>
        </el-table-column>

        <el-table-column label="小计" width="120">
          <template #default="scope">
            <b style="color: #f56c6c">
              ￥{{ ((scope.row.price || 0) * (scope.row.quantity || 0)).toFixed(2) }}
            </b>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <!-- 【关键修复】：这里传的是 scope.row.id -->
            <el-button 
              type="danger" 
              size="small" 
              link 
              icon="Delete"
              @click="handleRemove(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 底部结算 -->
      <div v-if="cartList.length > 0" class="cart-footer">
        <div class="total-bar">
          合计金额：<span class="total-price">￥ {{ totalPrice }}</span>
        </div>
        <el-button type="primary" size="large" @click="handleCheckout" :loading="submitLoading">
          生成订单并支付
        </el-button>
      </div>

      <el-empty v-else description="清单为空" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { Delete } from '@element-plus/icons-vue';

const router = useRouter();
const loading = ref(false);
const submitLoading = ref(false);
const cartList = ref([]);

// 计算总价
const totalPrice = computed(() => {
  return cartList.value.reduce((acc, item) => acc + ((item.price || 0) * (item.quantity || 0)), 0).toFixed(2);
});

// 1. 加载列表
const fetchCart = async () => {
  loading.value = true;
  try {
    const res = await request.get('/orders/cart');
    // 兼容 {"code":1, "data": [...]}
    cartList.value = res.data || [];
    console.log("当前购物车数据:", cartList.value);
  } finally {
    loading.value = false;
  }
};

// 2. 更新数量 PUT /orders/cart/{id}?quantity=x
const handleUpdateQuantity = async (row, val) => {
  const cartId = row.id; // 确定使用 id
  try {
    await request.put(`/orders/cart/${cartId}`, null, { params: { quantity: val } });
  } catch (e) {
    fetchCart(); // 失败刷新
  }
};

/**
 * 3. 移除商品 DELETE /orders/cart/{id}
 * 【核心修复逻辑】
 */
const handleRemove = (row) => {
  const cartId = row.id; // 从 row 中提取真正的 id 字段
  
  console.log("准备删除购物车项，数据内容:", row);
  console.log("提取到的主键 id 为:", cartId);

  if (!cartId) {
    ElMessage.error("错误：无法获取该行数据的 id");
    return;
  }

  ElMessageBox.confirm('确定要从清单中删除该食材吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // 发送请求到后端 /orders/cart/4 (假设id为4)
      const res = await request.delete(`/orders/cart/${cartId}`);
      if (res.code === 1 || res.code === 0) {
        ElMessage.success('已移出清单');
        fetchCart(); // 重新加载列表
      }
    } catch (err) {
      console.error("删除失败:", err);
    }
  });
};

// 4. 结算
const handleCheckout = async () => {
  submitLoading.value = true;
  try {
    const res = await request.post('/orders/cart/checkout', {
      deliveryAddressId: 1,
      paymentMethod: 1
    });
    if (res.code === 1) {
      ElMessage.success('下单成功！');
      router.push('/buyer-orders');
    }
  } finally {
    submitLoading.value = false;
  }
};

onMounted(fetchCart);
</script>

<style scoped>
.header-box { display: flex; justify-content: space-between; align-items: center; }
.cart-footer { margin-top: 20px; padding: 20px; border-top: 1px solid #eee; text-align: right; }
.total-price { font-size: 24px; font-weight: bold; color: #f56c6c; margin-right: 20px; }
</style>