<template>
  <div class="doctor-layout">
    <!-- 左侧菜单栏 -->
    <aside class="doctor-sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon v-if="!isCollapsed" class="logo-icon"><OfficeBuilding /></el-icon>
          <span v-if="!isCollapsed" class="logo-text">智能医疗</span>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/doctor/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <el-menu-item index="/doctor/consultation/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>
            <span style="flex: 1">在线问诊</span>
            <el-badge
              v-if="unreadCount > 0"
              :value="unreadCount > 99 ? '99+' : unreadCount"
              class="menu-badge"
            />
          </template>
        </el-menu-item>

        <el-menu-item index="/doctor/consultation">
          <el-icon><Document /></el-icon>
          <template #title>问诊管理</template>
        </el-menu-item>

        <el-menu-item index="/doctor/patients">
          <el-icon><UserFilled /></el-icon>
          <template #title>患者管理</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button
          text
          class="collapse-btn"
          @click="toggleCollapse"
        >
          <el-icon><component :is="isCollapsed ? Expand : Fold" /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="doctor-main">
      <!-- 顶部导航栏 -->
      <header class="doctor-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/doctor/home' }">工作台</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentPageTitle">{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="36" :src="authStore.userInfo?.avatar">
                {{ authStore.userInfo?.realName?.charAt(0) || authStore.userInfo?.username?.charAt(0) }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</div>
                <div class="user-role">医生</div>
              </div>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="doctor-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConsultationListApi } from '../../api/doctor'
import {
  HomeFilled,
  ChatDotRound,
  Document,
  UserFilled,
  User,
  SwitchButton,
  ArrowDown,
  OfficeBuilding,
  Expand,
  Fold,
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)
const unreadCount = ref(0)

// 加载未读消息数
const loadUnreadCount = async () => {
  try {
    const response = await getConsultationListApi({ current: 1, size: 100, status: 0 })
    const totalUnread = response.records?.reduce((sum, item) => sum + (item.unreadCount || 0), 0) || 0
    unreadCount.value = totalUnread
  } catch (error: unknown) {
    console.error('加载未读消息数失败:', error)
  }
}

// 定时刷新未读消息数
let unreadCountInterval: number | null = null

onMounted(() => {
  loadUnreadCount()
  // 每30秒刷新一次未读消息数
  unreadCountInterval = window.setInterval(() => {
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (unreadCountInterval) {
    clearInterval(unreadCountInterval)
  }
})

// 监听路由变化，刷新未读消息数
watch(() => route.path, () => {
  if (route.path.startsWith('/doctor')) {
    loadUnreadCount()
  }
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/doctor/consultation/chat')) {
    return '/doctor/consultation/chat'
  }
  return path
})

// 当前页面标题
const currentPageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/doctor/home': '',
    '/doctor/consultation/chat': '在线问诊',
    '/doctor/consultation': '问诊管理',
    '/doctor/patients': '患者管理',
  }
  return titleMap[route.path] || ''
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 处理下拉菜单命令
const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      await authStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error: unknown) {
      // 用户取消
    }
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中...')
  }
}
</script>

<style scoped>
.doctor-layout {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

/* 侧边栏 */
.doctor-sidebar {
  width: 240px;
  background: #304156;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.doctor-sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
}

.logo-text {
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border: none;
  overflow-y: auto;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  margin: 4px 8px;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff !important;
}

.menu-badge {
  margin-left: 8px;
}

.sidebar-menu :deep(.el-menu-item) {
  position: relative;
}

.sidebar-footer {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  color: #bfcbd9;
  font-size: 18px;
}

.collapse-btn:hover {
  color: #409eff;
}

/* 主内容区 */
.doctor-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.doctor-header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.arrow-icon {
  color: #909399;
  font-size: 12px;
}

/* 内容区域 */
.doctor-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f0f2f5;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .doctor-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(0);
  }

  .doctor-sidebar.collapsed {
    transform: translateX(-100%);
  }

  .doctor-main {
    margin-left: 0;
  }
}
</style>

