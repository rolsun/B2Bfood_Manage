<template>
  <!-- 路由占位符：根据当前 URL 自动显示对应的页面（登录页、首页等） -->
  <router-view />
</template>

<script setup>
// 这里不需要写额外逻辑
</script>

<style>
/* 基础样式重置，确保页面铺满全屏 */
body, html, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}
</style>