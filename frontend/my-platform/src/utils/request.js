import axios from 'axios';
import { ElMessage, ElLoading } from 'element-plus';

let loadingInstance = null;

const service = axios.create({
  baseURL: 'http://127.0.0.1:8080', 
  timeout: 10000
});

service.interceptors.request.use(config => {
  // 只在提交数据(非GET)时显示加载动画，显得灵动不笨重
  if (config.method !== 'get') {
    loadingInstance = ElLoading.service({ text: '处理中...', background: 'rgba(255, 255, 255, 0.7)' });
  }

  if (config.params && config.params.status == 4) {
    console.error("检测到非法参数 status=4，正在强行修正为 3！");
    config.params.status = 3;
  }



  const token = localStorage.getItem('token');
  if (token) {
    if (config.url.endsWith('/auth/logout')) {
      config.headers['Authorization'] = token;
    } else {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
  }
  return config;
}, error => Promise.reject(error));

service.interceptors.response.use(response => {
  if (loadingInstance) loadingInstance.close();
  return response.data; // 保持你已经调通的返回结构
}, error => {
  if (loadingInstance) loadingInstance.close();
  // 保持你要求的：直接弹出后端给的错误消息（如：用户不存在）
  const msg = error.response?.data?.message || error.response?.data?.msg || '服务连接异常';
  ElMessage.error(msg);
  return Promise.reject(error);
});

export default service;