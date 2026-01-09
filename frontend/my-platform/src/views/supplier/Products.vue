<template>
  <div class="supplier-products">
    <el-card>
      <template #header>
        <div class="header-flex">
          <span>我的供货商品库</span>
          <el-button type="success" icon="Plus" @click="openDialog()">发布新商品</el-button>
        </div>
      </template>

      <el-table :data="products" stripe>
        <el-table-column prop="productId" label="ID" width="70" />
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="price" label="基础单价">
          <template #default="scope">￥{{ scope.row.price }} / {{ scope.row.unit }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="当前库存" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架中' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.productId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑/新增弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.productId ? '修改商品信息' : '发布食材'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类目ID"><el-input-number v-model="form.categoryId" /></el-form-item>
        <el-form-item label="单价(元)"><el-input-number v-model="form.price" :precision="2" /></el-form-item>
        <el-form-item label="计量单位"><el-input v-model="form.unit" placeholder="斤/kg/箱" /></el-form-item>
        <el-form-item label="库存数量"><el-input-number v-model="form.stock" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const products = ref([]);
const dialogVisible = ref(false);
const form = ref({ name: '', categoryId: 1, price: 0, unit: '', stock: 0, supplier_id: localStorage.getItem('userId') });

const fetchMyProducts = async () => {
  const res = await request.get('/products/supplier');
  products.value = res.data.list;
};

const openDialog = (row = null) => {
  if (row) form.value = { ...row };
  else form.value = { name: '', categoryId: 1, price: 0, unit: '', stock: 0, supplier_id: localStorage.getItem('userId') };
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (form.value.productId) {
    await request.put(`/products/${form.value.productId}`, form.value);
  } else {
    await request.post('/products/add', form.value);
  }
  ElMessage.success('保存成功');
  dialogVisible.value = false;
  fetchMyProducts();
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该商品吗？此操作不可逆', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`/products/${id}`);
    ElMessage.success('删除成功');
    fetchMyProducts();
  });
};

onMounted(fetchMyProducts);
</script>

<style scoped>
.header-flex { display: flex; justify-content: space-between; align-items: center; }
</style>