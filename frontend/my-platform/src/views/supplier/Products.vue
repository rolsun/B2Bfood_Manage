<template>
  <div class="supplier-products">
    <el-card shadow="never">
      <template #header>
        <div class="header-box">
          <span class="title">我的供货食材库</span>
          <el-button type="success" icon="Plus" @click="handleOpenDialog()">发布新食材</el-button>
        </div>
      </template>

      <!-- 列表 -->
      <el-table :data="productList" v-loading="loading" stripe border>
        <!-- 重点：这里使用 productId -->
        <el-table-column label="食材ID" prop="productId" width="80" align="center" />
        
        <el-table-column label="缩略图" width="100" align="center">
          <template #default="scope">
            <el-image 
              :src="formatImg(scope.row.image)" 
              style="width: 50px; height: 50px; border-radius: 4px;" 
              fit="cover"
            >
              <template #error><div class="img-slot">无图</div></template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="食材名称" min-width="150" />
        
        <el-table-column label="供货价格" width="150">
          <template #default="scope">
            <span class="price-text">￥{{ scope.row.price }}</span> / {{ scope.row.unit }}
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="当前库存" width="100" align="center" />

        <el-table-column label="管理操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <!-- 修改和删除都传入完整的 scope.row -->
            <el-button link type="primary" icon="Edit" @click="handleOpenDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.productId ? '编辑食材信息' : '发布新食材'" width="500px">
      <el-form :model="form" label-width="100px" label-position="left">
        <el-form-item label="食材名称">
          <el-input v-model="form.name" placeholder="如：鲜活基围虾" />
        </el-form-item>
        <el-form-item label="所属类目">
          <el-select v-model="form.categoryId" placeholder="请选择类目" style="width:100%">
            <el-option v-for="c in categoryOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="供货单价">
          <el-input-number v-model="form.price" :precision="2" :step="0.5" style="width:100%" />
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="form.unit" placeholder="斤 / kg / 箱" />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="form.stock" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.image" placeholder="http://..." />
        </el-form-item>
        <el-form-item label="食材描述">
          <el-input v-model="form.description" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">提交保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import request from '../../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../store/user';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';

const userStore = useUserStore();
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const productList = ref([]);
const categoryOptions = ref([]);

// 核心表单：使用 productId 保持一致
const form = reactive({
  productId: null, 
  name: '',
  categoryId: 1,
  price: 0,
  unit: 'kg',
  stock: 100,
  description: '',
  image: '',
  supplier_id: userStore.userId
});

const formatImg = (url) => {
  if (!url) return '';
  return url.startsWith('http') ? url : `http://127.0.0.1:8080/${url}`;
};

// 获取商品列表
const fetchData = async () => {
  loading.value = true;
  try {
    // 接口：GET /products/supplier
    const res = await request.get('/products/supplier');
    productList.value = res.data.list || res.data || [];
    
    // 获取分类列表备选
    const catRes = await request.get('/categories');
    categoryOptions.value = catRes.data || [];
  } finally {
    loading.value = false;
  }
};

const handleOpenDialog = (row = null) => {
  if (row) {
    // 编辑：将行数据填入表单，重点是获取 productId
    Object.assign(form, row);
    console.log("准备编辑商品，ID:", form.productId);
  } else {
    // 新增：重置表单
    Object.assign(form, {
      productId: null,
      name: '',
      categoryId: 1,
      price: 0,
      unit: 'kg',
      stock: 100,
      description: '',
      image: '',
      supplier_id: userStore.userId
    });
  }
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!form.name) return ElMessage.warning('食材名称不能为空');
  
  submitLoading.value = true;
  try {
    // 逻辑：有 productId 则更新，无则新增
    if (form.productId) {
      // 接口：PUT /products/{productId}
      await request.put(`/products/${form.productId}`, form);
      ElMessage.success('食材信息更新成功');
    } else {
      // 接口：POST /products/add
      form.supplier_id = userStore.userId;
      await request.post('/products/add', form);
      ElMessage.success('食材发布成功');
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = (row) => {
  const pid = row.productId;
  if (!pid) return ElMessage.error("未找到商品ID");

  ElMessageBox.confirm(`确定要彻底删除食材 [${row.name}] 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 接口：DELETE /products/{productId}
      const res = await request.delete(`/products/${pid}`);
      if (res.code === 1 || res.code === 0) {
        ElMessage.success('食材已移除');
        fetchData();
      }
    } catch (err) {
      console.error("删除请求失败", err);
    }
  });
};

onMounted(fetchData);
</script>

<style scoped>
.header-box { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 18px; font-weight: bold; color: #409EFF; }
.price-text { color: #f56c6c; font-weight: bold; font-size: 16px; }
.img-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; font-size: 12px; }
</style>