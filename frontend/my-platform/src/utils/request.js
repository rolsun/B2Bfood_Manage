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

     // 检查后端返回的 code，如果是 0 表示业务失败（HTTP 200）
     if (response.data && response.data.code === 0) {
         const errorMsg = response.data.msg || '操作失败';
         ElMessage.error(errorMsg);
         return Promise.reject(new Error(errorMsg));
     }

     return response.data;
 }, error => {
     if (loadingInstance) loadingInstance.close();

     // 处理 HTTP 错误状态码（如 500、401 等）
     let msg = '服务连接异常';

     if (error.response?.data) {
         // 优先使用后端返回的错误信息
         msg = error.response.data.msg || error.response.data.message || msg;
     }

     ElMessage.error(msg);
     return Promise.reject(error);
 });

export default service;