<template>
  <div class="patient-layout">
    <!-- 顶部导航栏 -->
    <header class="patient-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="logo">
            <el-icon><ChatDotRound /></el-icon>
            <span>智能医疗助手</span>
          </h1>
        </div>
        <nav class="header-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            <el-icon><component :is="iconMap[item.icon] || ChatDotRound" /></el-icon>
            <span>{{ item.title }}</span>
          </router-link>
        </nav>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-avatar">
              <el-avatar :size="36" :src="authStore.userInfo?.avatar">
                {{ authStore.userInfo?.realName?.charAt(0) || authStore.userInfo?.username?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</span>
              <el-icon class="arrow-down"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="patient-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  ChatDotRound,
  Search,
  User,
  SwitchButton,
  ArrowDown,
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 图标映射，用于动态组件
const iconMap: Record<string, Component> = {
  HomeFilled,
  ChatDotRound,
  Search,
}

const navItems = computed(() => {
  const items: Array<{ path: string; title: string; icon: string }> = []
  
  if (authStore.hasPermission('patient:home')) {
    items.push({
      path: '/patient/home',
      title: '首页',
      icon: 'HomeFilled',
    })
  }
  
  if (authStore.hasPermission('patient:consultation')) {
    items.push({
      path: '/patient/consultation',
      title: '在线问诊',
      icon: 'ChatDotRound',
    })
  }
  
  if (authStore.hasPermission('patient:diagnosis')) {
    items.push({
      path: '/patient/diagnosis',
      title: '皮肤诊断',
      icon: 'Search',
    })
  }
  
  return items
})

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      await authStore.logout()
      ElMessage.success('退出登录成功')
      router.push('/login')
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    // TODO: 跳转到个人中心
    ElMessage.info('个人中心功能开发中...')
  }
}
</script>

<style scoped>
.patient-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

.patient-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
  cursor: pointer;
}

.logo .el-icon {
  font-size: 28px;
}

.header-nav {
  display: flex;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border-radius: 12px;
  color: #606266;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.nav-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: #fff;
  border-radius: 2px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: rgba(102, 126, 234, 0.1);
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.arrow-down {
  font-size: 12px;
  color: #909399;
}

.patient-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 70px);
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
    height: 60px;
  }

  .logo span {
    display: none;
  }

  .header-nav {
    gap: 4px;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 14px;
  }

  .nav-item span {
    display: none;
  }

  .user-name {
    display: none;
  }

  .patient-main {
    padding: 16px;
  }
}
</style>

