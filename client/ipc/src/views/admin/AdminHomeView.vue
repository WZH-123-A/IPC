<template>
  <div class="admin-home">
    <h1>管理后台</h1>
    <p>欢迎，{{ authStore.userInfo?.realName || authStore.userInfo?.username }} 管理员</p>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.admin-home {
  padding: 2rem;
}
</style>
