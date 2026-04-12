<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧：资料修改表单 -->
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span style="margin-left: 8px; font-weight: bold;">个人基本资料</span>
            </div>
          </template>
          
          <el-form :model="userForm" label-width="100px" v-loading="loading" style="max-width: 500px; margin-top: 10px;">
            <el-form-item label="头像">
              <div class="avatar-upload-wrapper">
                <image-upload 
                  dir="avatar" 
                  button-text="上传新头像"
                  :max-size="5"
                  @success="handleAvatarUploadSuccess" 
                />
                <el-image 
                  v-if="userForm.avatar" 
                  :src="formatAvatar(userForm.avatar)" 
                  style="width: 120px; height: 120px; margin-top: 10px; border-radius: 8px;" 
                  fit="cover"
                  :preview-src-list="[formatAvatar(userForm.avatar)]"
                >
                  <template #error>
                    <div class="img-slot">头像加载失败</div>
                  </template>
                </el-image>
              </div>
            </el-form-item>
            
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" placeholder="请输入新的用户名" clearable />
              <div class="form-tip">注意：修改用户名后需重新登录</div>
            </el-form-item>
            
            <el-form-item label="联系电话">
              <el-input v-model="userForm.phone" placeholder="请输入手机号码" clearable />
            </el-form-item>
            
            <el-form-item label="电子邮箱">
              <el-input v-model="userForm.email" placeholder="请输入邮箱地址" clearable />
            </el-form-item>
            
            <el-form-item label="联系地址">
              <el-input v-model="userForm.address" type="textarea" :rows="3" placeholder="请输入您的详细地址" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleUpdate" icon="Check">保存资料修改</el-button>
              <el-button @click="fetchProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 右侧：账号状态展示 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span style="margin-left: 8px; font-weight: bold;">账号状态</span>
            </div>
          </template>
          
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">注册时间</span>
              <span class="info-content">{{ userForm.createTime || '2026-01-01 00:00:00' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最后更新</span>
              <span class="info-content">{{ userForm.updateTime || '刚刚' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前身份</span>
              <span class="info-content">
                <el-tag :type="userStore.userType == 1 ? 'success' : 'warning'">
                  {{ userStore.userType == 1 ? '采购商' : '供应商' }}
                </el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">账户状态</span>
              <span class="info-content">
                <el-badge is-dot type="success" class="status-badge">正常运行中</el-badge>
              </span>
            </div>
          </div>
        </el-card>

        <el-alert
          title="重要提示"
          type="warning"
          description="如果您修改了【用户名】，当前的登录授权（Token）将会失效。系统会自动登出，请使用新用户名重新登录。"
          show-icon
          style="margin-top: 20px;"
          :closable="false"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useUserStore } from '../../store/user';
import { useRouter } from 'vue-router';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, InfoFilled, Check } from '@element-plus/icons-vue';
import ImageUpload from '@/components/ImageUpload.vue';

const userStore = useUserStore();
const router = useRouter();
const loading = ref(false);

const userForm = reactive({
  username: '',
  phone: '',
  email: '',
  address: '',
  avatar: '',
  createTime: '',
  updateTime: ''
});

// 记录修改前的原始用户名，用于对比
const originalUsername = ref('');

const fetchProfile = async () => {
  loading.value = true;
  try {
    const res = await request.get('/users/profile');
    if (res.code === 1) {
       const profileUsername = res.data.username || res.data.userName || '';
      userForm.username = profileUsername;
      userForm.phone = res.data.phone;
      userForm.email = res.data.email;
      userForm.address = res.data.address;
      userForm.avatar = res.data.avatar || '';
      userForm.createTime = res.data.createTime;
      userForm.updateTime = res.data.updateTime;
      // 记录初始用户名
      originalUsername.value = profileUsername;
      userStore.setProfile({ username: profileUsername, avatar: userForm.avatar });
    }
  } catch (err) {
    console.error("加载失败", err);
  } finally {
    loading.value = false;
  }
};

// 格式化头像 URL
const formatAvatar = (url) => {
  if (!url) return '';
  return url.startsWith('http') ? url : `http://127.0.0.1:8080/${url}`;
};

// 头像上传成功回调
const handleAvatarUploadSuccess = (data) => {
  userForm.avatar = data.url;
  ElMessage.success('头像上传成功');
};

const handleUpdate = async () => {
  if (!userForm.username) return ElMessage.warning('用户名不能为空');

  try {
    // 检查用户名是否发生了变化
    const isUsernameChanged = userForm.username !== originalUsername.value;

    const res = await request.put('/users/profile', {
      username: userForm.username,
      phone: userForm.phone,
      email: userForm.email,
      address: userForm.address,
      avatar: userForm.avatar
    });
    
    if (res.code === 1) {
      userStore.setProfile({ username: userForm.username, avatar: userForm.avatar });
      if (isUsernameChanged) {
        // 如果改了用户名，强制下线
        ElMessageBox.alert(
          `用户名已成功修改为 [${userForm.username}]。由于安全原因，您的登录会话已失效，请使用新用户名重新登录。`,
          '修改成功',
          {
            confirmButtonText: '前往登录页',
            callback: () => {
              userStore.clear();
              router.push('/login');
            }
          }
        );
      } else {
        ElMessage.success('资料更新成功');
        fetchProfile();
      }
    }
  } catch (err) {
    console.error("更新失败", err);
  }
};

onMounted(fetchProfile);
</script>

<style scoped>
.profile-container { padding: 5px; }
.card-header { display: flex; align-items: center; }
.form-tip { font-size: 12px; color: #f56c6c; margin-top: 4px; }
.info-list { padding: 10px 0; }
.info-item { display: flex; justify-content: space-between; align-items: center; padding: 15px 0; border-bottom: 1px dashed #ebeef5; }
.info-item:last-child { border-bottom: none; }
.info-label { color: #606266; font-size: 14px; }
.info-content { color: #303133; font-size: 14px; font-weight: 500; }
.status-badge { font-size: 13px; color: #67c23a; }
.avatar-upload-wrapper { display: flex; flex-direction: column; gap: 10px; }
.img-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; font-size: 12px; }
</style>