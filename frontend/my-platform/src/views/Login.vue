<template>
  <div class="login-wrapper">
    <!-- 背景装饰图（可选，这里用渐变代替） -->
    <div class="login-container">
      <!-- 顶部品牌标识 -->
      <div class="brand-header">
        <el-icon :size="40" color="#409EFF"><Shop /></el-icon>
        <h1 class="brand-title">Rolsun的食品采购市场</h1>
      </div>

      <el-card class="login-card" shadow="always">
        <div class="card-header">
          <h2 class="welcome-text">欢迎登录</h2>
          <p class="sub-text">请输入您的账号和密码进入系统</p>
        </div>

        <el-form :model="form" ref="loginFormRef" :rules="rules" label-position="top">
          <el-form-item label="账号" prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名/手机号" 
              prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              prefix-icon="Lock"
              show-password
              @keyup.enter="doLogin"
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-button link type="primary">忘记密码？</el-button>
          </div>

          <el-button 
            type="primary" 
            class="login-button" 
            :loading="loading" 
            @click="doLogin"
          >
            立即登录
          </el-button>
        </el-form>

        <div class="card-footer">
          <span>还没有账号？</span>
          <el-button link type="primary">立即注册</el-button>
        </div>
      </el-card>

      <div class="system-footer">
        © 2026 Rolsun Food Supply Chain Management. All Rights Reserved.
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { User, Lock, Shop } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const rememberMe = ref(false);

const form = reactive({
  username: '',
  password: ''
});

// 表单校验规则
const rules = {
  username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
};

const doLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请完整填写登录信息');
    return;
  }
  
  loading.value = true;
  try {
    const res = await request.post('/auth/login', form);
    userStore.setUserInfo(res);
    ElMessage.success({
      message: '登录成功，欢迎来到Rolsun食材市场',
      type: 'success'
    });
    router.push('/dashboard');
  } catch (err) {
    // 拦截器已处理错误提示
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 整个背景设置 */
.login-wrapper {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 现代感的深蓝色渐变背景 */
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  overflow: hidden;
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 20px;
  z-index: 1;
}

/* 品牌标识样式 */
.brand-header {
  text-align: center;
  margin-bottom: 30px;
}

.brand-title {
  color: #ffffff;
  font-size: 28px;
  margin-top: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 登录卡片样式 */
.login-card {
  border: none;
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 10px;
}

.card-header {
  text-align: center;
  margin-bottom: 25px;
}

.welcome-text {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.sub-text {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

/* 表单内选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  letter-spacing: 2px;
  background: linear-gradient(to right, #409EFF, #0072ff);
  border: none;
  transition: transform 0.2s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.card-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #666;
}

/* 底部版本号 */
.system-footer {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

/* 覆盖 Element Plus 默认样式 */
:deep(.el-input__wrapper) {
  padding: 8px 12px;
  border-radius: 6px;
}
</style>