<template>
  <div class="market-container">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 20px; font-weight: bold; color: #409EFF;">Rolsun 食品采购市场</span>
          <el-button type="primary" icon="Refresh" @click="fetchData">刷新食材列表</el-button>
        </div>
      </template>

      <el-table :data="productList" v-loading="loading" stripe border>
        <el-table-column prop="productId" label="ID" width="70" align="center" />
        <el-table-column label="食材图" width="100" align="center">
          <template #default="scope">
            <el-image :src="getImgUrl(scope.row.image)" style="width: 50px; height: 50px; border-radius: 4px;" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食材名称" />
        <el-table-column prop="price" label="单价" width="120">
          <template #default="scope">￥{{ scope.row.price }} / {{ scope.row.unit }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="success" size="small" @click="addToCart(scope.row)">加入购物车</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage } from 'element-plus';

const productList = ref([]);
const loading = ref(false);

const getImgUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return `http://127.0.0.1:8080/${url}`;
};

const fetchData = async () => {
  loading.value = true;
  try {
    // 【关键修复】：请求时必须携带 page 和 limit 参数，防止后端 SQL 报错
    const res = await request.get('/products', {
      params: {
        page: 1,      // 默认第一页
        limit: 20,    // 默认每页20条
        keyword: '',  // 搜索词为空
        categoryId: null // 类目为空
      }
    });

    console.log("后端返回原始数据:", res);

    // 适配你的 {"code":1, "data": [...]} 结构
    if (res.code === 1) {
      // 兼容处理：如果后端返回的是分页对象 {list: [], total: 10}
      if (res.data.list) {
        productList.value = res.data.list;
      } else {
        // 如果后端直接返回的是数组
        productList.value = res.data;
      }
    }
  } catch (err) {
    console.error("请求过程发生错误:", err);
  } finally {
    loading.value = false;
  }
};

const addToCart = async (item) => {
  await request.post('/orders/cart/add', {
    productId: item.productId,
    quantity: 1
  });
  ElMessage.success(`[${item.name}] 已加入清单`);
};

onMounted(fetchData);
</script>