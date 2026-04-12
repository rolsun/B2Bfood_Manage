<template>
  <div class="register-page">
    <!-- 背景装饰球 (保持与登录页一致的视觉语言) -->
    <div class="circles">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="register-container">
      <el-card class="register-card" shadow="always">
        <!-- 头部品牌标识 -->
        <div class="register-header">
          <div class="logo-icon">
            <el-icon :size="32" color="#fff"><Shop /></el-icon>
          </div>
          <h2 class="title">加入 Rolsun 市场</h2>
          <p class="subtitle">创建一个账号以开始您的食材采购或供应业务</p>
        </div>

        <!-- 注册表单 -->
        <el-form 
          :model="regForm" 
          ref="regFormRef" 
          :rules="rules" 
          label-position="top" 
          class="custom-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="regForm.username" 
              placeholder="请设置您的登录账号" 
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="设置密码" prop="password">
            <el-input 
              v-model="regForm.password" 
              type="password" 
              placeholder="至少 6 位字母或数字" 
              :prefix-icon="Lock"
              show-password
              size="large"
            />
          </el-form-item>

          <el-form-item label="您的身份角色" prop="userType">
            <el-radio-group v-model="regForm.userType" class="role-selector">
              <el-radio :label="1" border class="role-item">
                <div class="role-content">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>我是采购商</span>
                </div>
              </el-radio>
              <el-radio :label="2" border class="role-item">
                <div class="role-content">
                  <el-icon><Box /></el-icon>
                  <span>我是供应商</span>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <div class="agreement">
            <el-checkbox v-model="agreed">
              我已阅读并同意 <el-button link type="primary">《服务协议》</el-button> 与 <el-button link type="primary">《隐私政策》</el-button>
            </el-checkbox>
          </div>

          <el-button 
            type="primary" 
            class="register-btn" 
            :loading="loading" 
            @click="handleRegister"
          >
            注 册 账 号
          </el-button>

          <div class="register-footer">
            <span>已有 Rolsun 账号？</span>
            <el-button link type="primary" @click="$router.push('/login')">立即登录</el-button>
          </div>
        </el-form>
      </el-card>

      <!-- 底部版权信息 -->
      <div class="footer-info">
        Rolsun Food Management System v1.0
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import { User, Lock, Shop, ShoppingCart, Box } from '@element-plus/icons-vue';

const router = useRouter();
const loading = ref(false);
const agreed = ref(true);

const regForm = reactive({
  username: '',
  password: '',
  userType: 1
});

const rules = {
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置您的密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择您的身份', trigger: 'change' }
  ]
};

const handleRegister = async () => {
  if (!agreed.value) {
    return ElMessage.warning('请阅读并勾选服务协议');
  }

  loading.value = true;
  try {
    const res = await request.post('/auth/register', regForm);
    // 适配 code=1 或 code=0 成功
    if (res.code === 1 || res.code === 0) {
      ElMessage.success('注册成功！正在前往登录页面...');
      setTimeout(() => {
        router.push('/login');
      }, 1500);
    }
  } catch (err) {
    console.error('注册失败', err);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 页面基础样式 */
.register-page {
  height: 100vh;
  width: 100vw;
  background: #0f172a; /* 与登录页一致的深色底 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
}

/* 装饰背景 */
.circles {
  position: absolute;
  width: 100%;
  height: 100%;
}
.circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.5;
}
.circle-1 {
  width: 400px; height: 400px;
  background: #3b82f6;
  top: -100px; right: -50px;
}
.circle-2 {
  width: 300px; height: 300px;
  background: #8b5cf6;
  bottom: -50px; left: -50px;
}

/* 注册卡片容器 */
.register-container {
  z-index: 10;
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.register-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  padding: 10px 20px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3);
}

/* 头部样式 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}
.logo-icon {
  width: 56px;
  height: 56px;
  background: #2563eb;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 8px 15px rgba(37, 99, 235, 0.3);
}
.title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}
.subtitle {
  font-size: 14px;
  color: #64748b;
  margin-top: 8px;
}

/* 角色选择器美化 */
.role-selector {
  display: flex;
  gap: 15px;
  width: 100%;
}
.role-item {
  flex: 1;
  height: 60px;
  margin: 0 !important;
  border-radius: 12px !important;
  transition: all 0.3s;
}
.role-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  line-height: 1;
  padding-top: 5px;
}
.role-content span {
  font-size: 13px;
}

/* 表单细节 */
.agreement {
  margin-bottom: 25px;
}
:deep(.el-checkbox__label) {
  font-size: 13px;
  color: #64748b;
}

.register-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: #2563eb;
  border: none;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.2);
}

.register-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #64748b;
}

.footer-info {
  text-align: center;
  margin-top: 25px;
  color: rgba(255, 255, 255, 0.4);
  font-size: 12px;
  letter-spacing: 1px;
}

/* 输入框统一风格 */
:deep(.el-input__wrapper) {
  background-color: #f8fafc;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 5px 15px;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #2563eb;
}
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #475569;
  padding-bottom: 4px;
}
</style>