<template>
  <el-container style="height: 100vh;">
    <!-- 左侧菜单栏 -->
    <el-aside width="240px" style="background-color: #304156;">
      <div style="height: 60px; line-height: 60px; text-align: center; color: #fff; font-weight: bold; background: #2b2f3a;">
        Rolsun 食材管理系统
      </div>
      <el-menu 
        background-color="#304156" 
        text-color="#fff" 
        active-text-color="#ffd04b" 
        router 
        :default-active="$route.path"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Menu /></el-icon>控制台首页
        </el-menu-item>
        
        <!-- 采购商菜单：仅 userType 为 1 时显示 -->
        <template v-if="Number(userStore.userType) === 1">
          <el-menu-item index="/market">
            <el-icon><Shop /></el-icon>食材市场
          </el-menu-item>
        
          <el-menu-item index="/buyer-orders">
            <el-icon><List /></el-icon>我的订单
          </el-menu-item>
          <el-menu-item index="/cart">
            <el-icon><ShoppingCart /></el-icon>我的购物车
          </el-menu-item>
        </template>

        <!-- 供应商菜单：仅 userType 为 2 时显示 -->
        <template v-else-if="Number(userStore.userType) === 2">
          <el-menu-item index="/supplier-products">
            <el-icon><Box /></el-icon>商品管理
          </el-menu-item>
          <el-menu-item index="/supplier-orders">
            <el-icon><Promotion /></el-icon>发货处理
          </el-menu-item>
        </template>

        <!-- 管理员菜单：仅 userType 为 3 时显示 -->
        <template v-else-if="Number(userStore.userType) === 3">
          <el-menu-item index="/user-manage">
            <el-icon><UserFilled /></el-icon>系统用户管理
          </el-menu-item>
        </template>

        <el-menu-item index="/wallet">
          <el-icon><Wallet /></el-icon>我的钱包
        </el-menu-item>
        
        <el-menu-item @click="handleLogout" style="margin-top: 50px; color: #f56c6c;">
          <el-icon><SwitchButton /></el-icon>退出登录
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 右侧顶栏 -->
      <el-header style="background: #fff; border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: flex-end; padding-right: 20px;">
        <div style="display: flex; align-items: center;">
          <el-icon style="margin-right: 8px;"><User /></el-icon>
          <span style="font-size: 14px;">
            欢迎您：<b style="color: #409EFF;">{{ userStore.username }}</b> 
            <el-tag size="small" style="margin-left: 8px;">{{ roleText }}</el-tag>
          </span>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main style="background-color: #f5f7f9;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { Menu, Shop, List, Box, Promotion, Wallet, SwitchButton, User, UserFilled,ShoppingCart } from '@element-plus/icons-vue';

const userStore = useUserStore();
const router = useRouter();

// 【核心修复】：根据 userType 数字返回对应的身份文字
const roleText = computed(() => {
  const type = Number(userStore.userType);
  console.log("当前渲染的角色类型:", type); // 调试用
  if (type === 1) return '采购商';
  if (type === 2) return '供应商';
  if (type === 3) return '管理员';
  return '未识别角色';
});

const handleLogout = async () => {
  try {
    await request.post('/auth/logout');
  } finally {
    userStore.clear();
    router.push('/login');
  }
};
</script>

<style scoped>
.el-header {
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
</style>