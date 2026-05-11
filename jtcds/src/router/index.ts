import { createRouter, createWebHistory } from 'vue-router'
import PriceComparison from '../pages/PriceComparison.vue'
import Home from '../pages/Home.vue'
import BlindBox from '../pages/BlindBox.vue'
import LoginRegister from '../LoginRegister.vue'
import ApiTest from '../pages/ApiTest.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/home' },
    { path: '/home', name: 'Home', component: Home },
    { path: '/price', name: 'PriceComparison', component: PriceComparison },
    { path: '/blindbox', name: 'BlindBox', component: BlindBox },
    // 登录/注册页面路由
    { path: '/auth', name: 'LoginRegister', component: LoginRegister },
    // API测试页面路由
    { path: '/api-test', name: 'ApiTest', component: ApiTest },
  ],
})

export default router
