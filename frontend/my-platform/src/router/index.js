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
      { path: 'cart', component: () => import('../views/purchaser/Cart.vue') },
      { path: 'after-sales', component: () => import('../views/common/AfterSales.vue') },
      { path: 'user-manage', component: () => import('../views/admin/Users.vue') },
      { path: 'category-manage', component: () => import('../views/admin/Categories.vue') },
      { path: 'profile', component: () => import('../views/common/Profile.vue') },
      { path: '/register', name: 'Register', component: () => import('../views/Register.vue') }
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
 * 路由守卫逻辑
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const whitelist = ['/login', '/register']; // 白名单列表
  
  if (whitelist.includes(to.path)) {
    next();
  } else if (!token) {
    next('/login');
  } else {
    next();
  }
});

export default router;