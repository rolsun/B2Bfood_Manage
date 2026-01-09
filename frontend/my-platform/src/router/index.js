import { createRouter, createWebHistory } from 'vue-router';
import MainLayout from '../layout/MainLayout.vue';

const routes = [
  { 
    path: '/login', 
    name: 'Login',
    component: () => import('../views/Login.vue') 
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'market', component: () => import('../views/purchaser/Market.vue') },
      { path: 'buyer-orders', component: () => import('../views/purchaser/Orders.vue') },
      { path: 'supplier-products', component: () => import('../views/supplier/Products.vue') },
      { path: 'supplier-orders', component: () => import('../views/supplier/Orders.vue') },
      { path: 'wallet', component: () => import('../views/common/Wallet.vue') },
      { path: 'cart', component: () => import('../views/purchaser/Cart.vue') }
    ]
  },
  // 捕获所有不存在的路由，重定向到首页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

/**
 * 核心修复：路由守卫逻辑
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  // 1. 如果要去登录页，直接放行
  if (to.path === '/login') {
    next();
  } 
  // 2. 如果没有 Token，且访问的不是登录页，强制跳转到 /login
  else if (!token) {
    next('/login');
  } 
  // 3. 有 Token，正常放行
  else {
    next();
  }
});

export default router;