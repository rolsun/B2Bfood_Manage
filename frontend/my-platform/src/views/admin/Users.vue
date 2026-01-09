<template>
  <el-card>
    <template #header><h2>系统用户管理</h2></template>
    <el-table :data="userList">
      <el-table-column prop="id" label="UID" width="80" />
      <el-table-column prop="username" label="登录账号" />
      <el-table-column label="身份">
        <template #default="scope">
          <el-tag>{{ scope.row.usertype === 1 ? '采购商' : scope.row.usertype === 2 ? '供应商' : '管理员' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <el-switch :model-value="scope.row.status === 1" />
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';

const userList = ref([]);
const fetchData = async () => {
  const res = await request.get('/users', { params: { page: 1, limit: 10 } });
  userList.value = res.data.list;
};

onMounted(fetchData);
</script>