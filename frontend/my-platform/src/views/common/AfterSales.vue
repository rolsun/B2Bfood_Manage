<template>
  <div class="after-sales-container">
    <div class="page-header">
      <div class="header-left">
        <h1>售后纠纷管理</h1>
        <p>
          当前身份：
          <el-tag size="small" :type="userStore.userType == 1 ? 'success' : 'warning'" effect="dark">
            {{ userStore.userType == 1 ? '采购商' : '供应商' }}
          </el-tag>
        </p>
      </div>
      <!-- 只有采购商有权发起新申请 -->
      <el-button v-if="userStore.userType == 1" type="danger" icon="Warning" round @click="handleOpenApply">
        发起售后申请
      </el-button>
    </div>

    <el-card shadow="never" class="list-card">
      <div class="table-toolbar">
        <span class="table-title">
          {{ userStore.userType == 1 ? '📋 我的申请记录' : '📥 待我处理的投诉' }}
        </span>
        <el-button icon="Refresh" circle @click="fetchData" />
      </div>

      <div class="table-area" v-loading="loading">
        <el-table :data="afterSalesList" border stripe class="modern-table">
          <el-table-column label="售后单号" prop="afterSalesSn" min-width="180" />
          <el-table-column label="关联订单" prop="orderSn" min-width="180" />
          
          <el-table-column label="类型" width="100" align="center">
            <template #default="scope">
              <el-tag effect="plain" type="info">{{ getTypeName(scope.row.afterSalesType) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="详细说明" prop="applicationContent" show-overflow-tooltip />

          <el-table-column label="当前状态" width="100" align="center">
            <template #default="scope">
              <!-- 状态 1 代表待处理 -->
              <el-tag :type="scope.row.status == 1 ? 'warning' : 'success'" round>
                {{ scope.row.status == 1 ? '处理中' : '已关闭' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="提交时间" prop="createTime" width="170" align="center" />

          <el-table-column label="管理操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <!-- 供应商专属：处理并关闭 -->
              <el-button 
                v-if="userStore.userType == 2 && scope.row.status == 1" 
                type="primary" size="small" round @click="handleProcess(scope.row)"
              >确认结案</el-button>

              <!-- 采购商专属：撤回 -->
              <el-button 
                v-if="userStore.userType == 1 && scope.row.status == 1" 
                type="info" size="small" link @click="handleCancel(scope.row)"
              >撤回申请</el-button>
              
              <span v-if="scope.row.status != 1" style="color: #94a3b8; font-size: 12px;">已归档</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空数据提示 -->
        <el-empty v-if="afterSalesList.length === 0" :description="userStore.userType == 2 ? '太棒了，目前没有待处理的投诉' : '暂无任何售后申请记录'" />
      </div>
    </el-card>

    <!-- 采购商发起申请弹窗 -->
    <el-dialog v-model="applyVisible" title="发起售后申请" width="480px" destroy-on-close>
      <el-form :model="applyForm" label-width="90px">
        <el-form-item label="订单编号">
          <el-input v-model="applyForm.complainedOrder" placeholder="请填写订单页面的编号" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="applyForm.complainedSupplier" placeholder="填写供应商名称" />
        </el-form-item>
        <el-form-item label="处理方式">
          <el-select v-model="applyForm.afterSalesType" style="width: 100%">
            <el-option label="退货退款" :value="1" />
            <el-option label="仅退款" :value="2" />
            <el-option label="换货" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input v-model="applyForm.applicationContent" type="textarea" :rows="3" placeholder="请描述遇到的具体问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitApply" :loading="submitting" style="width: 100%">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useUserStore } from '../../store/user';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Warning, Refresh } from '@element-plus/icons-vue';

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const applyVisible = ref(false);
const afterSalesList = ref([]);

const applyForm = reactive({
  complainedOrder: '',
  complainedSupplier: '',
  afterSalesType: 1,
  applicationContent: ''
});

// 获取数据逻辑
const fetchData = async () => {
  loading.value = true;
  try {
    // 核心逻辑：
    // 采购商（1）：请求 my-list
    // 供应商（2）：请求 pending (专注待处理数据)
    const url = userStore.userType == 2 ? '/api/after-sales/pending' : '/api/after-sales/my-list';
    
    console.log(`[AfterSales] Requesting: ${url}`);
    const res = await request.get(url);
    
    if (res.code === 1) {
      // 兼容 data 是数组 或 data.list 是数组的情况
      const rawData = res.data;
      if (Array.isArray(rawData)) {
        afterSalesList.value = rawData;
      } else if (rawData && Array.isArray(rawData.list)) {
        afterSalesList.value = rawData.list;
      } else {
        afterSalesList.value = [];
      }
      console.log(`[AfterSales] Loaded count: ${afterSalesList.value.length}`);
    }
  } catch (err) {
    console.error("加载售后数据失败", err);
  } finally {
    loading.value = false;
  }
};

const handleOpenApply = () => {
  applyForm.complainedOrder = '';
  applyForm.applicationContent = '';
  applyVisible.value = true;
};

const submitApply = async () => {
  if (!applyForm.complainedOrder) return ElMessage.warning('请填写订单编号');
  submitting.value = true;
  try {
    const res = await request.post('/api/after-sales/submit', applyForm);
    if (res.code === 1) {
      ElMessage.success('售后申请已成功提交');
      applyVisible.value = false;
      fetchData();
    }
  } finally {
    submitting.value = false;
  }
};

const handleProcess = (row) => {
  ElMessageBox.confirm('确定已处理该售后申请并关闭该单据吗？', '确认处理').then(async () => {
    // 优先读取 id 字段
    const targetId = row.id || row.afterSalesId;
    const res = await request.put(`/api/after-sales/process/${targetId}`);
    if (res.code === 1) {
      ElMessage.success('售后申请已处理结案');
      fetchData();
    }
  });
};

const handleCancel = (row) => {
  const targetId = row.id || row.afterSalesId;
  ElMessageBox.confirm('确定要撤回该申请吗？').then(async () => {
    const res = await request.delete(`/api/after-sales/cancel/${targetId}`);
    if (res.code === 1) {
      ElMessage.info('已成功撤回');
      fetchData();
    }
  });
};

const getTypeName = (t) => ({ 1: '退货退款', 2: '仅退款', 3: '换货' }[t] || '其他');

onMounted(fetchData);
</script>

<style scoped>
.after-sales-container { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.header-left h1 { margin: 0; font-size: 26px; font-weight: 800; color: #1e293b; }
.header-left p { color: #64748b; margin-top: 5px; font-size: 14px; }

.list-card { border-radius: 20px !important; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.05) !important; }

.table-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.table-title { font-weight: 700; color: #334155; font-size: 16px; }

.modern-table :deep(.el-table__header) th { background: #f8fafc; color: #475569; font-weight: 700; height: 50px; }
</style>