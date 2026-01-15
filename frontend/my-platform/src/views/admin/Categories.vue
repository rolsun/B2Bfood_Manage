<template>
  <div class="category-manage">
    <el-card shadow="never" class="modern-card">
      <template #header>
        <div class="flex-between">
          <span class="card-title">食材分类管理</span>
          <el-button type="primary" icon="Plus" @click="handleOpenDialog()">新增类目</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="分类ID" prop="id" width="120" align="center" />
        <el-table-column label="类目名称" prop="name" min-width="200">
           <template #default="scope">
             <b style="color: #334155;">{{ scope.row.name }}</b>
           </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="220" align="center" />
        <el-table-column label="管理操作" width="200" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleOpenDialog(scope.row)">修改名称</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row.id)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑类目' : '创建类目'" width="420px" destroy-on-close>
      <el-form :model="form" label-width="80px" style="padding: 10px 0;">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="如：时令果蔬" @keyup.enter="submitForm" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';

const loading = ref(false);
const visible = ref(false);
const list = ref([]);
const form = reactive({ id: null, name: '' });

const fetchData = async () => {
  loading.value = true;
  const res = await request.get('/categories');
  list.value = res.data || [];
  loading.value = false;
};

const handleOpenDialog = (row = null) => {
  if (row) {
    form.id = row.id; 
    form.name = row.name;
  } else {
    form.id = null; 
    form.name = '';
  }
  visible.value = true;
};

const submitForm = async () => {
  if (!form.name) return ElMessage.warning('名称必填');
  
  // 【核心修复】：根据 id 决定走 PUT(修改) 还是 POST(新建)
  if (form.id) {
    await request.put(`/categories/${form.id}`, { name: form.name });
    ElMessage.success('类目已更新');
  } else {
    await request.post('/categories/create', { name: form.name });
    ElMessage.success('类目已创建');
  }
  visible.value = false;
  fetchData();
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除此分类吗？').then(async () => {
    await request.delete(`/categories/${id}`);
    ElMessage.success('已删除');
    fetchData();
  });
};

onMounted(fetchData);
</script>

<style scoped>
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 20px; font-weight: 800; color: #1e293b; }
.modern-card { border-radius: 16px !important; }
</style>