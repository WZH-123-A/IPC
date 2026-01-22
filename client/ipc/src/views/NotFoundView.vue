<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面不存在</h2>
      <p class="error-description">抱歉，您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getDefaultRouteByRole } from '../router/permission'

const router = useRouter()
const authStore = useAuthStore()

const goHome = () => {
  if (authStore.isAuthenticated && authStore.userRole) {
    const defaultRoute = getDefaultRouteByRole(authStore.userRole)
    router.push({ name: defaultRoute })
  } else {
    router.push({ name: 'login' })
  }
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--bg-color-page);
}

.not-found-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: var(--primary-color);
  margin: 0;
  line-height: 1;
}

.error-title {
  font-size: 32px;
  color: var(--text-color-primary);
  margin: 20px 0;
}

.error-description {
  font-size: 16px;
  color: var(--text-color-secondary);
  margin-bottom: 30px;
}
</style>
