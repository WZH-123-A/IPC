import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    if (authStore.isAuthenticated) {
      next()
    } else {
      // 未登录，跳转到登录页
      next({ name: 'login', query: { redirect: to.fullPath } })
    }
  } else {
    // 如果已登录，访问登录页时跳转到首页
    if (to.name === 'login' && authStore.isAuthenticated) {
      next({ name: 'home' })
    } else {
      next()
    }
  }
})

export default router
