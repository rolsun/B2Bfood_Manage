<template>
  <div class="dashboard-container">
    <el-card shadow="never" style="margin-bottom: 20px;">
      <h2 style="margin: 0 0 10px 0;">系统控制台</h2>
      <div style="color: #666;">
        欢迎来到 Rolsun 食材管理系统，您当前的身份是：
        <el-tag size="small" effect="plain">{{ roleName }}</el-tag>
      </div>
    </el-card>

    <!-- 顶部四个统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">商品总数 (种)</div>
          <div class="value">{{ totalProducts }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">待处理订单</div>
          <div class="value" style="color: #E6A23C;">{{ pendingOrders }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">钱包余额</div>
          <div class="value" style="color: #67C23A;">￥ {{ walletBalance }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">系统总用户</div>
          <div class="value" style="color: #409EFF;">{{ totalUsers }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下方详细统计列表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 角色分布列表 -->
      <el-col :span="10">
        <el-card header="用户角色分布">
          <el-table :data="stats.userRoleStats" stripe size="small">
            <el-table-column prop="roleName" label="角色名称" />
            <el-table-column prop="count" label="数量">
              <template #default="scope">
                <b>{{ scope.row.count }}</b> 人
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 类目分布详情 -->
      <el-col :span="14">
        <el-card header="食材类目库存详情">
          <div class="category-grid">
            <div v-for="cat in stats.productCategoryStats" :key="cat.categoryId" class="cat-item">
              <span class="cat-name">{{ cat.categoryName }}</span>
              <span class="cat-count">{{ cat.count }} 种</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '../store/user';
import request from '../utils/request';

const userStore = useUserStore();
const stats = ref({ userRoleStats: [], productCategoryStats: [] });
const walletBalance = ref(0);
const pendingOrders = ref(0); // 统计接口中暂未包含此字段，先设为0或等后续联调

const roleName = computed(() => {
  const type = Number(userStore.userType);
  if (type === 1) return '采购商';
  if (type === 2) return '供应商';
  if (type === 3) return '管理员';
  return '未知';
});

// 计算属性：计算总用户数
const totalUsers = computed(() => {
  return stats.value.userRoleStats.reduce((sum, item) => sum + (item.count || 0), 0);
});

// 计算属性：计算商品总数（各分类商品种类相加）
const totalProducts = computed(() => {
  return stats.value.productCategoryStats.reduce((sum, item) => sum + (item.count || 0), 0);
});

const fetchData = async () => {
  try {
    // 1. 获取综合统计数据
    const res = await request.get('/api/statistics/overview');
    stats.value = res.data;

    // 2. 获取个人钱包余额 (因为overview是全局统计，不含个人隐私)
    const walletRes = await request.get('/wallet/info');
    walletBalance.value = walletRes.data.balance || 0;

  } catch (err) {
    console.error("数据加载失败:", err);
  }
};

onMounted(fetchData);
</script>

<style scoped>
.stat-card {
  text-align: left;
}
.stat-card .label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-card .value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.cat-item {
  background: #f8f9fb;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  text-align: center;
}
.cat-name { display: block; color: #606266; font-size: 13px; }
.cat-count { display: block; color: #409EFF; font-size: 16px; font-weight: bold; margin-top: 4px; }
</style>