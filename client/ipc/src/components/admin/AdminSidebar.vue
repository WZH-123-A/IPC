<template>
  <aside class="admin-sidebar">
    <el-menu
      :default-active="activeMenu"
      router
      class="sidebar-menu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/admin/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/admin/users">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/roles">
        <el-icon><UserFilled /></el-icon>
        <span>角色管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/permissions">
        <el-icon><Key /></el-icon>
        <span>权限管理</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled, User, UserFilled, Key } from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-sidebar {
  width: 200px;
  background-color: #304156;
  overflow-y: auto;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}
</style>

