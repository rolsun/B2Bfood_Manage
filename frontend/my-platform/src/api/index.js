import request from '../utils/request';

// 认证
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  logout: () => request.post('/auth/logout'),
};

// 商品
export const productApi = {
  add: (data) => request.post('/products/add', data),
  getSupplierList: () => request.get('/products/supplier'),
  getBuyerList: (params) => request.get('/products', { params }),
  update: (id, data) => request.put(`/products/${id}`, data),
  delete: (id) => request.delete(`/products/${id}`),
};

// 订单
export const orderApi = {
  create: (data) => request.post('/orders', data),
  getList: (params) => request.get('/orders', { params }),
  getDetail: (id) => request.get(`/orders/${id}`),
  pay: (id) => request.post(`/orders/pay/${id}`),
  supplierPending: (params) => request.get('/orders/supplier/pending', { params }),
  delivery: (id, data) => request.post(`/orders/${id}/delivery`, data),
};

// 钱包
export const walletApi = {
  getInfo: () => request.get('/wallet/info'),
  recharge: (data) => request.post('/wallet/recharge', data),
  withdraw: (data) => request.post('/wallet/withdraw', data),
};

// 用户管理（管理员用）
export const userApi = {
  list: (params) => request.get('/users', { params }),
  profile: () => request.get('/users/profile'),
};

// 统计
export const statApi = {
  getOverview: () => request.get('/api/statistics/overview'),
};

// 文件上传
export const fileApi = {
    upload: (file,dir = 'upload') =>{
        const formData = new FormData();
        formData.append('file', file);
        formData.append('dir',dir);
        return request.post('/api/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });

    },
    delete: (url) => request.delete('/api/file/delete',{ params: {url}}),
};