<template>
  <div class="doctor-home">
    <h1>医生首页</h1>
    <p>欢迎，{{ authStore.userInfo?.realName || authStore.userInfo?.username }} 医生</p>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.doctor-home {
  padding: 2rem;
}
</style>
