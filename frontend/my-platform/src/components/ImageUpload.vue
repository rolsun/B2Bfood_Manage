<template>
  <div class="upload-container">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :limit="limit"
      :accept="accept"
      :show-file-list="showFileList"
      class="upload-element"
    >
      <slot name="trigger">
        <el-button type="primary" :size="buttonSize">
          <i class="el-icon-upload"></i> {{ buttonText }}
        </el-button>
      </slot>
      
      <template #tip>
        <slot name="tip">
          <div v-if="showTip" class="upload-tip">
            支持 jpg/png/gif 格式，大小不超过 {{ maxSize }}MB
          </div>
        </slot>
      </template>
    </el-upload>
    
    <!-- 图片预览 -->
    <el-dialog v-model="dialogVisible" title="图片预览" width="600px">
      <img :src="previewUrl" alt="预览图片" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  // 上传目录
  dir: {
    type: String,
    default: 'upload'
  },
  // 最大文件大小（MB）
  maxSize: {
    type: Number,
    default: 10
  },
  // 限制上传数量
  limit: {
    type: Number,
    default: 1
  },
  // 接受的文件类型
  accept: {
    type: String,
    default: 'image/*'
  },
  // 按钮大小
  buttonSize: {
    type: String,
    default: 'default'
  },
  // 按钮文字
  buttonText: {
    type: String,
    default: '上传图片'
  },
  // 是否显示文件列表
  showFileList: {
    type: Boolean,
    default: false
  },
  // 是否显示提示
  showTip: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['success', 'error']);

const baseURL = 'http://127.0.0.1:8080';
const uploadUrl = computed(() => `${baseURL}/api/file/upload`);

const headers = computed(() => {
  const token = localStorage.getItem('token');
  return {
    'Authorization': token ? `Bearer ${token}` : ''
  };
});

const dialogVisible = ref(false);
const previewUrl = ref('');

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt10M = file.size / 1024 / 1024 < props.maxSize;

  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    return false;
  }
  
  if (!isLt10M) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB！`);
    return false;
  }
  
  return true;
};

// 上传成功
const handleSuccess = (response, uploadFile) => {
  if (response.code === 1) {
    ElMessage.success('上传成功');
    emit('success', response.data);
  } else {
    ElMessage.error(response.msg || '上传失败');
  }
};

// 上传失败
const handleError = (err) => {
  console.error('上传失败:', err);
  ElMessage.error('上传失败，请重试');
  emit('error', err);
};

// 预览图片
const handlePreview = (file) => {
  previewUrl.value = file.response?.data?.url || URL.createObjectURL(file.raw);
  dialogVisible.value = true;
};
</script>

<style scoped>
.upload-container {
  display: inline-block;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
  margin-top: 5px;
}
</style>
