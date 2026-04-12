<template>
  <div class="login-page">
    <!-- 背景动态流光 -->
    <div class="bg-blobs">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>

    <div class="login-card-wrapper">
      <!-- 左侧：品牌视觉区 -->
      <div class="brand-panel">
        <div class="brand-content">
          <div class="logo-wrapper">
            <el-icon :size="50" class="logo-icon"><Shop /></el-icon>
          </div>
          <h1 class="brand-name">Rolsun</h1>
          <div class="brand-divider"></div>
          <p class="brand-slogan">数字化食品供应链管理专家</p>
          
          <ul class="feature-tags">
            <li><el-icon><CircleCheckFilled /></el-icon> 产地直供选购</li>
            <li><el-icon><CircleCheckFilled /></el-icon> 安全透明结算</li>
            <li><el-icon><CircleCheckFilled /></el-icon> 实时物流追踪</li>
          </ul>
        </div>
        <!-- 装饰性文字背景 -->
        <div class="bg-text">FOOD MARKET</div>
      </div>

      <!-- 右侧：登录表单区 -->
      <div class="form-panel">
        <div class="form-container">
          <header class="form-header">
            <h2 class="title">欢迎登录</h2>
            <p class="subtitle">Enter your credentials to access the market</p>
          </header>

          <el-form 
            :model="loginForm" 
            :rules="rules" 
            ref="formRef" 
            label-position="top"
            @submit.prevent
          >
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="用户名 / 手机号" 
                :prefix-icon="User"
                size="large"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码" 
                :prefix-icon="Lock"
                show-password
                size="large"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-button link type="primary" class="forget-pwd">忘记密码？</el-button>
            </div>

            <el-button 
              type="primary" 
              class="submit-btn" 
              :loading="loading" 
              @click="handleLogin"
            >
              登 录 <el-icon class="el-icon--right"><ArrowRightBold /></el-icon>
            </el-button>
          </el-form>

          <footer class="form-footer">
            <span class="footer-text">还没有账号？</span>
            <el-button link type="primary" @click="$router.push('/register')">立即免费注册</el-button>
          </footer>
        </div>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="page-footer">
      © 2026 Rolsun Food Supply Chain Management System. All Rights Reserved.
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { User, Lock, Shop, CircleCheckFilled, ArrowRightBold } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);
const rememberMe = ref(false);

const loginForm = reactive({
  username: '',
  password: ''
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
};

const handleLogin = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await request.post('/auth/login', loginForm);
        // 如果后端 code === 1，走这里
        userStore.setUserInfo(res);
        ElMessage({
          message: `欢迎回来，${res.data.username}`,
          type: 'success',
          customClass: 'modern-message'
        });
        router.push('/dashboard');
      } catch (err) {
        // 报错逻辑已在 request.js 中自动处理：弹出后端返回的 message (如：该用户不存在)
        console.error('Login process error');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
/* 基础容器 */
.login-page {
  height: 100vh;
  width: 100vw;
  background-color: #0c111d;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, sans-serif;
}

/* 动态流光背景 */
.bg-blobs {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 0;
}
.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: move 20s infinite alternate;
}
.blob-1 { width: 600px; height: 600px; background: #2563eb; top: -100px; left: -100px; }
.blob-2 { width: 500px; height: 500px; background: #7c3aed; bottom: -100px; right: -100px; animation-delay: -5s; }
.blob-3 { width: 400px; height: 400px; background: #0ea5e9; top: 40%; left: 50%; animation-delay: -10s; }

@keyframes move {
  from { transform: translate(0, 0) scale(1); }
  to { transform: translate(50px, 50px) scale(1.1); }
}

/* 登录卡片 */
.login-card-wrapper {
  z-index: 10;
  width: 1000px;
  height: 620px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 28px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 40px 100px -20px rgba(0, 0, 0, 0.5);
}

/* 左侧面板 */
.brand-panel {
  flex: 1;
  background: linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #fff;
  overflow: hidden;
}
.logo-wrapper {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 25px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}
.brand-name { font-size: 52px; font-weight: 800; margin: 0; letter-spacing: -1px; }
.brand-divider { width: 40px; height: 4px; background: #60a5fa; margin: 20px 0; border-radius: 2px; }
.brand-slogan { font-size: 18px; color: #bfdbfe; margin-bottom: 40px; }
.feature-tags { list-style: none; padding: 0; margin: 0; }
.feature-tags li { 
  display: flex; align-items: center; gap: 12px; margin-bottom: 18px;
  font-size: 15px; color: rgba(255,255,255,0.85);
}
.bg-text {
  position: absolute; bottom: -20px; left: -20px;
  font-size: 100px; font-weight: 900; opacity: 0.05;
  white-space: nowrap; pointer-events: none;
}

/* 右侧表单 */
.form-panel {
  flex: 1;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}
.form-container { width: 100%; max-width: 360px; }
.form-header { margin-bottom: 35px; }
.form-header .title { font-size: 32px; font-weight: 700; color: #111827; margin: 0; }
.form-header .subtitle { font-size: 14px; color: #6b7280; margin-top: 10px; }

.form-options {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;
}
.forget-pwd { font-size: 13px; }

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  background: #2563eb;
  border: none;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.25);
  transition: all 0.3s;
}
.submit-btn:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 15px 25px -5px rgba(37, 99, 235, 0.3);
}

.form-footer {
  text-align: center; margin-top: 35px; border-top: 1px solid #f3f4f6; padding-top: 25px;
}
.footer-text { color: #6b7280; font-size: 14px; }

.page-footer {
  position: absolute; bottom: 25px;
  color: rgba(255,255,255,0.4); font-size: 12px; letter-spacing: 0.5px;
}

/* 覆盖 Element Plus 输入框样式 */
:deep(.el-input__wrapper) {
  background-color: #f9fafb !important;
  box-shadow: none !important;
  border: 1.5px solid #f3f4f6;
  border-radius: 12px;
  padding: 8px 15px;
  transition: all 0.2s;
}
:deep(.el-input__wrapper.is-focus) {
  background-color: #fff !important;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1) !important;
}
:deep(.el-form-item__label) {
  font-weight: 600; color: #374151; font-size: 14px; padding-bottom: 6px;
}
</style>