<template>
  <el-container class="app-layout">
    <el-aside width="260px" class="aside-menu">
      <div class="aside-logo">
        <div class="logo-icon">R</div>
        <span class="logo-text">ROLSUN <small>FOOD</small></span>
      </div>

      <el-scrollbar>
        <el-menu :default-active="$route.path" background-color="transparent" text-color="#94a3b8" active-text-color="#ffffff" router class="custom-menu">
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon><span>工作台概览</span>
          </el-menu-item>

          <!-- 1. 采购商专属 (userType 1) -->
          <template v-if="Number(userStore.userType) === 1">
            <div class="menu-sep">采购协作</div>
            <el-menu-item index="/market"><el-icon><Shop /></el-icon>食材市场</el-menu-item>
            <el-menu-item index="/cart"><el-icon><ShoppingCart /></el-icon>采购清单</el-menu-item>
            <el-menu-item index="/buyer-orders"><el-icon><List /></el-icon>我的订单</el-menu-item>
          </template>

          <!-- 2. 供应商专属 (userType 2) -->
          <template v-else-if="Number(userStore.userType) === 2">
            <div class="menu-sep">供应业务</div>
            <el-menu-item index="/supplier-products"><el-icon><Box /></el-icon>供货管理</el-menu-item>
            <el-menu-item index="/supplier-orders"><el-icon><Van /></el-icon>发货任务</el-menu-item>
          </template>

          <!-- 3. 管理员专属 (userType 3) - 核心修复：显示管理入口 -->
          <template v-else-if="Number(userStore.userType) === 3">
            <div class="menu-sep">平台治理</div>
            <el-menu-item index="/user-manage">
              <el-icon><UserFilled /></el-icon><span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/category-manage">
              <el-icon><Collection /></el-icon><span>类目维护</span>
            </el-menu-item>
          </template>

          <!-- 4. 资产与服务：管理员完全不显示，防止 403 -->
          <template v-if="Number(userStore.userType) !== 3">
            <div class="menu-sep">资产服务</div>
            <el-menu-item index="/wallet"><el-icon><Wallet /></el-icon><span>我的钱包</span></el-menu-item>
            <el-menu-item index="/after-sales"><el-icon><Service /></el-icon><span>售后投诉</span></el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>

      <div class="aside-footer" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon><span>安全退出系统</span>
      </div>
    </el-aside>

    <el-container>
      <el-header class="header-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>管理中心</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title || '当前页' }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right" @click="$router.push('/profile')" style="cursor: pointer;">
          <el-tag :type="userStore.userType == 3 ? 'danger' : 'success'" round effect="dark" style="margin-right: 12px">
            {{ {1:'采购商', 2:'供应商', 3:'管理员'}[userStore.userType] }}
          </el-tag>
          <span class="user-display">{{ userStore.username }}</span>
          <el-avatar :size="32" :src="formatAvatar(userStore.avatar)" icon="UserFilled" />
        </div>
      </el-header>
      <el-main class="content-area">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { ElMessageBox, ElMessage } from 'element-plus';
import { DataLine, Shop, ShoppingCart, List, Box, Van, Wallet, Service, UserFilled, Collection, SwitchButton } from '@element-plus/icons-vue';

const userStore = useUserStore();
const router = useRouter();

const formatAvatar = (url) => {
  if (!url) return '';
  return url.startsWith('http') ? url : `http://127.0.0.1:8080/${url}`;
};

const loadProfile = async () => {
  try {
    const res = await request.get('/users/profile');
    if (res.code === 1 && res.data) {
      userStore.setProfile(res.data);
    }
  } catch (error) {
    console.error('加载用户资料失败', error);
  }
};

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出 B2B 食材管理系统吗？', '提示', { type: 'warning' }).then(async () => {
    try { await request.post('/auth/logout'); } finally {
      userStore.clear();
      router.push('/login');
      ElMessage.success('已安全退出');
    }
  });
};

onMounted(loadProfile);
</script>

<style scoped>
.app-layout { height: 100vh; background: #f8fafc; }
.aside-menu { background: #0f172a; display: flex; flex-direction: column; }
.aside-logo { height: 80px; padding: 0 25px; display: flex; align-items: center; gap: 12px; background: #1e293b; color: #fff; font-weight: 800; font-size: 20px; }
.logo-icon { width: 35px; height: 35px; background: #3b82f6; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-weight: 900; }
.custom-menu { border: none; padding: 0 15px; }
:deep(.el-menu-item) { border-radius: 10px; margin-bottom: 5px; height: 50px; }
:deep(.el-menu-item.is-active) { background-color: #2563eb !important; color: #fff; }
.menu-sep { padding: 25px 20px 10px; font-size: 11px; color: #475569; text-transform: uppercase; font-weight: bold; }
.aside-footer { padding: 25px; color: #94a3b8; cursor: pointer; border-top: 1px solid rgba(255,255,255,0.05); display: flex; align-items: center; gap: 10px; }
.header-bar { background: #fff; border-bottom: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: space-between; padding: 0 30px; }
.user-display { font-size: 14px; font-weight: 600; color: #1e293b; margin-right: 12px; }
.content-area { padding: 30px; }
</style>