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
      <template v-for="menuItem in menuItems" :key="menuItem.index">
        <!-- 有子菜单的情况 -->
        <el-sub-menu
          v-if="menuItem.children && menuItem.children.length > 0"
          :index="menuItem.index"
        >
          <template #title>
            <el-icon v-if="getIcon(menuItem.permissionCode)">
              <component :is="getIcon(menuItem.permissionCode)" />
            </el-icon>
            <span>{{ menuItem.title }}</span>
          </template>
          <el-menu-item v-for="child in menuItem.children" :key="child.index" :index="child.index">
            <el-icon v-if="getIcon(child.permissionCode)">
              <component :is="getIcon(child.permissionCode)" />
            </el-icon>
            <span>{{ child.title }}</span>
          </el-menu-item>
        </el-sub-menu>
        <!-- 单个菜单项 -->
        <el-menu-item v-else :index="menuItem.index">
          <el-icon v-if="getIcon(menuItem.permissionCode)">
            <component :is="getIcon(menuItem.permissionCode)" />
          </el-icon>
          <span>{{ menuItem.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCurrentUserMenusApi } from '../../api/auth'
import {
  convertPermissionTreeToMenuItems,
  permissionToIconMap,
  type MenuItem,
} from '../../utils/permission'
import type { PermissionTreeNode } from '../../api/admin/permission'
import { useAuthStore } from '../../stores/auth'
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

const getIcon = (permissionCode: string) => {
  const iconName = permissionToIconMap[permissionCode]
  return iconName ? iconMap[iconName] : null
}

/**
 * 从权限树中提取所有按钮权限编码
 */
const extractButtonPermissions = (tree: PermissionTreeNode[]): string[] => {
  const buttonPermissions: string[] = []

  const traverse = (nodes: PermissionTreeNode[]) => {
    for (const node of nodes) {
      // 如果是按钮权限（permissionType = 2），添加到列表中
      if (node.permissionType === 2) {
        buttonPermissions.push(node.permissionCode)
      }
      // 递归处理子节点
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    }
  }

  traverse(tree)
  return buttonPermissions
}

// 加载菜单权限
const loadMenuPermissions = async () => {
  try {
    const menuTree: PermissionTreeNode[] = await getCurrentUserMenusApi()
    menuItems.value = convertPermissionTreeToMenuItems(menuTree)

    const buttonPermissions = extractButtonPermissions(menuTree)

    if (authStore.userInfo) {
      const buttonPermissionPatterns = [
        ':add',
        ':edit',
        ':delete',
        ':query',
        ':view',
        ':assign',
        ':reset-password',
        ':batch-delete',
        ':create',
        ':update',
      ]

      const localPermissions = authStore.userInfo.permissions || []

      const nonButtonPermissions = localPermissions.filter((permission) => {
        return !buttonPermissionPatterns.some((pattern) => permission.endsWith(pattern))
      })

      const allPermissions = [...new Set([...nonButtonPermissions, ...buttonPermissions])]

      const permissionsChanged =
        allPermissions.length !== localPermissions.length ||
        !allPermissions.every((perm) => localPermissions.includes(perm)) ||
        !localPermissions.every((perm) => allPermissions.includes(perm))

      if (permissionsChanged) {
        authStore.userInfo = {
          ...authStore.userInfo,
          permissions: allPermissions,
        }

        const userInfoStr = JSON.stringify(authStore.userInfo)
        if (localStorage.getItem('token')) {
          localStorage.setItem('userInfo', userInfoStr)
        } else {
          sessionStorage.setItem('userInfo', userInfoStr)
        }
      }
    }
  } catch (error) {
    console.error('Failed to load menu permissions:', error)
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
