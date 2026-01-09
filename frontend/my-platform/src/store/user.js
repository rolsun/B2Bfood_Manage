import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    // 初始化时从本地加载，并转为数字
    token: localStorage.getItem('token') || '',
    userType: localStorage.getItem('userType') ? Number(localStorage.getItem('userType')) : null, 
    username: localStorage.getItem('username') || '',
    userId: localStorage.getItem('userId') || ''
  }),
  actions: {
    setUserInfo(res) {
      // 适配你的登录响应结构: res.data 包含用户信息
      const loginData = res.data;
      
      this.token = loginData.token;
      // 【关键修复】：强制转换为数字类型，避免字符串比较失效
      this.userType = Number(loginData.userType); 
      this.username = loginData.username;
      this.userId = loginData.userId;

      // 存入本地
      localStorage.setItem('token', this.token);
      localStorage.setItem('userType', String(this.userType));
      localStorage.setItem('username', this.username);
      localStorage.setItem('userId', this.userId);
      
      console.log("Store 角色已更新为:", this.userType, "类型:", typeof this.userType);
    },
    clear() {
      this.token = '';
      this.userType = null;
      this.username = '';
      this.userId = '';
      localStorage.clear();
    }
  }
});