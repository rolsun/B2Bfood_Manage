<template>
  <div class="user-manage">
    <el-card shadow="never" class="modern-card">
      <template #header>
        <div class="flex-between">
          <span class="card-title">全系统用户账号管理</span>
          <div class="table-tools">
            <el-radio-group v-model="query.userType" size="default" @change="fetchUsers">
              <el-radio-button :label="1">采购商</el-radio-button>
              <el-radio-button :label="2">供应商</el-radio-button>
              <el-radio-button :label="3">管理员</el-radio-button>
            </el-radio-group>
            <el-button type="primary" icon="Refresh" @click="fetchUsers" style="margin-left: 15px;">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="userList" v-loading="loading" border stripe class="clean-table">
        <el-table-column label="用户ID" prop="id" width="100" align="center" />
        
        <el-table-column label="登录账号 (userName)" min-width="150">
          <template #default="scope">
            <span style="font-weight: 600;">{{ scope.row.userName || scope.row.username }}</span>
          </template>
        </el-table-column>

        <el-table-column label="账户身份" width="130" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.usertype == 3 ? 'danger' : 'success'" effect="light">
              {{ {1:'采购商', 2:'供应商', 3:'管理员'}[scope.row.usertype] }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <!-- 修复：确定使用 row.id 且不能删自己 -->
            <el-button 
              v-if="scope.row.id != userStore.userId && scope.row.usertype != 3" 
              type="danger" size="small" plain icon="Delete"
              @click="handleDelete(scope.row)"
            >删除账号</el-button>
            <span v-else style="color: #94a3b8; font-size: 12px;">系统保护</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 动态分页：支持手动输入 -->
      <div class="pagination-footer">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.limit"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchUsers"
          @current-change="fetchUsers"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../store/user';
import { Refresh, Delete } from '@element-plus/icons-vue';

const userStore = useUserStore();
const loading = ref(false);
const userList = ref([]);
const total = ref(0);

const query = reactive({ page: 1, limit: 10, userType: 1 });

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await request.get('/users', { params: query });
    if (res.code === 1) {
      userList.value = res.data.list || res.data || [];
      total.value = res.data.total || userList.value.length;
    }
  } finally {
    loading.value = false;
  }
};

const handleDelete = (row) => {
  const targetId = row.id;
  const rolePath = row.usertype == 1 ? 'buyer' : 'supplier';

  ElMessageBox.confirm(`确定要彻底注销用户 [${row.userName || row.username}] 吗？`, '注销确认', {
    type: 'error',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  }).then(async () => {
    // 修复：使用 targetId 并根据角色决定路径
    const res = await request.delete(`/users/${rolePath}/${targetId}`);
    if (res.code === 1) {
      ElMessage.success('该账号已移除');
      fetchUsers();
    }
  });
};

onMounted(fetchUsers);
</script>

<style scoped>
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 20px; font-weight: 800; color: #1e293b; }
.pagination-footer { margin-top: 30px; display: flex; justify-content: flex-end; }
.modern-card { border-radius: 16px !important; }
</style>