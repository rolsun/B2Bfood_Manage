import axios from 'axios';
import { ElMessage } from 'element-plus';

const service = axios.create({
  baseURL: 'http://127.0.0.1:8080', 
  timeout: 10000
});

// 请求拦截
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    // 登录成功后，除了登出，所有接口必须带 Bearer
    if (config.url.endsWith('/auth/logout')) {
      config.headers['Authorization'] = token;
    } else {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
  }
  return config;
}, error => Promise.reject(error));

// 响应拦截
service.interceptors.response.use(response => {
  // 直接返回后端最原始的 JSON 数据包
  // 这样在页面上 res.code 就是 1，res.data 就是那串商品数组
  return response.data;
}, error => {
  console.dir(error);
  let msg = '服务器连接失败';
  if (error.response) {
    if (error.response.status === 403) msg = '权限不足 (403): 请检查账号角色';
    else if (error.response.status === 401) msg = '未登录或Token失效';
  }
  ElMessage.error(msg);
  return Promise.reject(error);
});

export default service;