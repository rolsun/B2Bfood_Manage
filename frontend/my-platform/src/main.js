import { createApp } from 'vue'
import App from './App.vue'

// 1. 导入路由
import router from './router'
// 2. 导入 Pinia
import { createPinia } from 'pinia'
// 3. 导入 Element Plus 及其样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 4. (可选) 如果你用了 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册插件
app.use(router)
app.use(createPinia())
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')