<template>
  <div class="dashboard-wrapper">
    <div class="welcome-bar">
      <h1>👋 平台运行实况</h1>
      <p>管理员 {{ userStore.username }}，欢迎回来。以下是 ROLSUN 市场的全维度统计数据。</p>
    </div>

    <!-- 管理员的核心指标 -->
    <el-row :gutter="25">
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card blue-gradient">
          <div class="stat-main">
            <div class="stat-info">
              <div class="label">系统活跃用户总数</div>
              <div class="value">{{ totalUsers }} <small>人</small></div>
            </div>
            <el-icon class="icon-bg"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card green-gradient">
          <div class="stat-main">
            <div class="stat-info">
              <div class="label">全平台在售食材种类</div>
              <div class="value">{{ totalProducts }} <small>种</small></div>
            </div>
            <el-icon class="icon-bg"><Shop /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 管理员专属：可视化大图表 -->
    <el-row :gutter="25" style="margin-top: 30px;">
      <el-col :span="16">
        <el-card header="库存分类分布 (Bar Chart)" shadow="never" class="chart-card">
          <div id="barChart" style="height: 450px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card header="角色构成比例 (Pie Chart)" shadow="never" class="chart-card">
          <div id="pieChart" style="height: 450px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '../store/user';
import request from '../utils/request';
import * as echarts from 'echarts';

const userStore = useUserStore();
const rawData = ref({ userRoleStats: [], productCategoryStats: [] });

const totalUsers = computed(() => rawData.value.userRoleStats.reduce((a, b) => a + (b.count || 0), 0));
const totalProducts = computed(() => rawData.value.productCategoryStats.reduce((a, b) => a + (b.count || 0), 0));

onMounted(async () => {
  // 1. 只请求宏观统计接口 (这个接口管理员有权访问)
  const res = await request.get('/api/statistics/overview');
  if (res.data) {
    rawData.value = res.data;
    renderCharts(res.data);
  }
});

const renderCharts = (data) => {
  // 饼图
  const pie = echarts.init(document.getElementById('pieChart'));
  pie.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', icon: 'circle', textStyle: { color: '#64748b' } },
    series: [{
      type: 'pie', radius: ['55%', '75%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 12, borderColor: '#fff', borderWidth: 4 },
      label: { show: false },
      data: data.userRoleStats.map(i => ({ name: i.roleName, value: i.count })),
      color: ['#6366f1', '#10b981', '#f59e0b']
    }]
  });

  // 柱状图
  const bar = echarts.init(document.getElementById('barChart'));
  bar.setOption({
    grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { 
      type: 'category', 
      data: data.productCategoryStats.map(i => i.categoryName),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b', interval: 0, rotate: 25 }
    },
    yAxis: { 
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }
    },
    tooltip: { trigger: 'axis' },
    series: [{ 
      data: data.productCategoryStats.map(i => i.count),
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3b82f6' },
          { offset: 1, color: '#60a5fa' }
        ]),
        borderRadius: [8, 8, 0, 0]
      }
    }]
  });
};
</script>

<style scoped>
.dashboard-wrapper { padding: 5px; }
.welcome-bar { margin-bottom: 40px; }
.welcome-bar h1 { margin: 0; font-size: 28px; font-weight: 800; color: #0f172a; }
.welcome-bar p { color: #64748b; margin-top: 8px; font-size: 15px; }

/* 指标卡片美化 */
.stat-card { border: none; border-radius: 24px !important; overflow: hidden; position: relative; }
.stat-main { padding: 35px; display: flex; justify-content: space-between; align-items: center; color: #fff; z-index: 2; position: relative; }
.blue-gradient { background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); }
.green-gradient { background: linear-gradient(135deg, #059669 0%, #10b981 100%); }

.stat-info .label { font-size: 14px; opacity: 0.8; font-weight: 600; margin-bottom: 12px; }
.stat-info .value { font-size: 42px; font-weight: 900; }
.stat-info .value small { font-size: 18px; font-weight: normal; margin-left: 5px; }

.icon-bg { font-size: 100px; position: absolute; right: -20px; bottom: -20px; opacity: 0.15; transform: rotate(-15deg); }

.chart-card { border-radius: 24px !important; border: 1px solid #f1f5f9 !important; }
:deep(.el-card__header) { border-bottom: 1px solid #f1f5f9; font-weight: 800; color: #334155; padding: 20px 25px; }
</style>