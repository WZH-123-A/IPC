<template>
  <div class="patient-home">
    <h1>患者首页</h1>
    <p>欢迎，{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</p>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.patient-home {
  padding: 2rem;
}
</style>
