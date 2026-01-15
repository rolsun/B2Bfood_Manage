<template>
  <div class="market-container">
    <el-card shadow="never" class="filter-bar">
      <div class="flex-between">
        <h2>发现优质食材</h2>
        <div class="search-box">
          <el-input v-model="query.keyword" placeholder="搜索食材名称..." @keyup.enter="fetchData" style="width: 320px;">
            <template #append><el-button icon="Search" @click="fetchData" /></template>
          </el-input>
          <el-badge :value="cartNum" class="m-l-20">
            <el-button type="warning" icon="ShoppingCart" circle @click="$router.push('/cart')" />
          </el-badge>
        </div>
      </div>
    </el-card>

    <div class="product-grid" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="6" v-for="p in productList" :key="p.productId">
          <div class="product-item">
            <div class="image-box">
              <el-image :src="p.image" fit="cover" />
              <div class="price-tag">￥{{ p.price }}</div>
            </div>
            <div class="info-box">
              <div class="name">{{ p.name }}</div>
              <div class="meta">单位: {{ p.unit }} | 库存: {{ p.stock }}</div>
              <div class="actions">
                <el-input-number v-model="p.buyNum" :min="1" size="small" />
                <el-button type="primary" size="small" circle icon="Plus" @click="addCart(p)" />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="query.page" :total="total" background layout="prev, pager, next" @current-change="fetchData" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElNotification } from 'element-plus';

const loading = ref(false);
const productList = ref([]);
const total = ref(0);
const cartNum = ref(0);
const query = reactive({ page: 1, limit: 12, keyword: '' });

const fetchData = async () => {
  loading.value = true;
  const res = await request.get('/products', { params: query });
  // 保持你调通的分页逻辑
  productList.value = (res.data.list || res.data).map(i => ({ ...i, buyNum: 1 }));
  total.value = res.data.total || productList.value.length;
  loading.value = false;
};

const addCart = async (p) => {
  await request.post('/orders/cart/add', { productId: p.productId, quantity: p.buyNum });
  ElNotification({ title: '添加成功', message: `${p.name} 已入清单`, type: 'success' });
  refreshCart();
};

const refreshCart = async () => {
  const res = await request.get('/orders/cart');
  cartNum.value = res.data?.length || 0;
};

onMounted(() => { fetchData(); refreshCart(); });
</script>

<style scoped>
.filter-bar { margin-bottom: 25px; border-radius: 16px !important; }
.product-item { background: #fff; border-radius: 16px; overflow: hidden; border: 1px solid #eef2f7; transition: 0.3s; margin-bottom: 20px; }
.product-item:hover { transform: translateY(-5px); box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1); }
.image-box { height: 180px; position: relative; overflow: hidden; }
.image-box .el-image { width: 100%; height: 100%; transition: 0.5s; }
.product-item:hover .el-image { transform: scale(1.1); }
.price-tag { position: absolute; top: 12px; right: 12px; background: #ef4444; color: #fff; padding: 4px 10px; border-radius: 8px; font-weight: bold; font-size: 14px; }
.info-box { padding: 15px; }
.name { font-weight: 700; font-size: 16px; color: #1e293b; margin-bottom: 5px; }
.meta { font-size: 12px; color: #94a3b8; margin-bottom: 15px; }
.actions { display: flex; justify-content: space-between; align-items: center; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 30px; }
.m-l-20 { margin-left: 20px; }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
</style>