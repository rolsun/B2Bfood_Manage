<template>
  <router-view v-slot="{ Component }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
:root {
  --primary-color: #2563eb;
  --bg-body: #f8fafc;
  --text-main: #1e293b;
  --border-radius: 12px;
}

body {
  margin: 0;
  padding: 0;
  background-color: var(--bg-body);
  color: var(--text-main);
  font-family: 'Inter', -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* 全局卡片阴影增强 */
.el-card {
  border-radius: var(--border-radius) !important;
  border: 1px solid rgba(0,0,0,0.05) !important;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02), 0 2px 4px -1px rgba(0,0,0,0.01) !important;
  transition: all 0.3s ease !important;
}

.el-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05) !important;
}

/* 页面切换动画 */
.page-fade-enter-active, .page-fade-leave-active {
  transition: all 0.25s ease;
}
.page-fade-enter-from { opacity: 0; transform: translateY(10px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-10px); }

/* 滚动条美化 */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
::-webkit-scrollbar-track { background: transparent; }
</style>