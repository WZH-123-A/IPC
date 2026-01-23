<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <AdminHeader />

    <div class="admin-container">
      <!-- 左侧菜单 -->
      <AdminSidebar />

      <!-- 主内容区 -->
      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import AdminHeader from '../../components/admin/AdminHeader.vue'
import AdminSidebar from '../../components/admin/AdminSidebar.vue'
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.admin-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>

