<template>
  <aside class="admin-sidebar">
    <el-menu
      :default-active="activeMenu"
      router
      class="sidebar-menu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      :unique-opened="false"
    >
      <!-- 固定首页入口，方便回到管理后台首页 -->
      <el-menu-item index="/admin/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <MenuItemComponent
        v-for="menuItem in menuItems"
        :key="menuItem.index"
        :menu-item="menuItem"
        :icon-map="iconMap"
        :permission-to-icon-map="permissionToIconMap"
      />
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  convertPermissionTreeToMenuItems,
  permissionToIconMap,
  type MenuItem,
} from '../../utils/permission'
import type { UserPermissionTreeNode } from '../../api/userPermissions'
import { filterPermissionsByType } from '../../api/userPermissions'
import { useAuthStore } from '../../stores/auth'
import MenuItemComponent from './MenuItem.vue'
import {
  HomeFilled,
  User,
  UserFilled,
  Key,
  Setting,
  Document,
  List,
  DocumentCopy,
  Avatar,
  ChatLineRound,
  Message,
  ChatDotRound,
  Star,
  Search,
  Picture,
  DocumentChecked,
  CollectionTag,
  Reading,
  Folder,
  PriceTag,
  DataAnalysis,
  TrendCharts,
} from '@element-plus/icons-vue'

const route = useRoute()
const authStore = useAuthStore()
const menuItems = ref<MenuItem[]>([])
const iconMap: Record<string, unknown> = {
  HomeFilled,
  User,
  UserFilled,
  Key,
  Setting,
  Document,
  List,
  DocumentCopy,
  Avatar,
  ChatLineRound,
  Message,
  ChatDotRound,
  Star,
  Search,
  Picture,
  DocumentChecked,
  CollectionTag,
  Reading,
  Folder,
  PriceTag,
  DataAnalysis,
  TrendCharts,
}

const activeMenu = computed(() => route.path)

// 加载菜单权限
const loadMenuPermissions = () => {
  try {
    // 优先从 authStore 获取权限树
    let permissionTree: UserPermissionTreeNode[] | null = null

    if (authStore.userInfo?.permissions) {
      permissionTree = authStore.userInfo.permissions
    } else {
      // 如果 store 中没有，尝试从本地存储获取
      const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          permissionTree = userInfo.permissions || null
        } catch (e) {
          console.error('解析用户信息失败:', e)
        }
      }
    }

    if (!permissionTree || permissionTree.length === 0) {
      console.warn('未找到权限树数据')
      menuItems.value = []
      return
    }

    // 过滤出菜单权限（permissionType === 1）
    const menuTree = filterPermissionsByType(permissionTree, 1)
    
    // 转换为菜单项
    menuItems.value = convertPermissionTreeToMenuItems(menuTree)
  } catch (error) {
    console.error('Failed to load menu permissions:', error)
    menuItems.value = []
  }
}

// 监听权限刷新事件
const handlePermissionRefresh = () => {
  loadMenuPermissions()
}

onMounted(() => {
  loadMenuPermissions()
  // 监听权限刷新事件
  window.addEventListener('permission-refresh', handlePermissionRefresh)
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('permission-refresh', handlePermissionRefresh)
})
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
