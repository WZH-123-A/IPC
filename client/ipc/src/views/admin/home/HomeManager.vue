<template>
  <div class="admin-home">
    <!-- 欢迎横幅 -->
    <section class="welcome-banner">
      <div class="welcome-bg" />
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎回来，{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</h1>
        <p class="welcome-subtitle">IPC 智能问诊管理平台 · 管理后台</p>
        <div class="welcome-time">
          <el-icon class="time-icon"><Clock /></el-icon>
          <span>{{ currentTime }}</span>
        </div>
      </div>
    </section>

    <!-- 数据概览（仅展示数据，不跳转、不与权限关联） -->
    <section class="stats-section">
      <h2 class="section-title">数据概览</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="stat-card stat-card-user" shadow="hover">
            <div class="stat-inner">
              <div class="stat-icon-wrap">
                <el-icon class="stat-icon"><UserFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.userTotal }}</div>
                <div class="stat-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="stat-card stat-card-role" shadow="hover">
            <div class="stat-inner">
              <div class="stat-icon-wrap">
                <el-icon class="stat-icon"><Key /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.roleTotal }}</div>
                <div class="stat-label">角色总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="stat-card stat-card-consultation" shadow="hover">
            <div class="stat-inner">
              <div class="stat-icon-wrap">
                <el-icon class="stat-icon"><ChatLineRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.sessionTotal }}</div>
                <div class="stat-label">问诊会话</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="stat-card stat-card-knowledge" shadow="hover">
            <div class="stat-inner">
              <div class="stat-icon-wrap">
                <el-icon class="stat-icon"><Reading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.contentTotal }}</div>
                <div class="stat-label">知识库内容</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="stat-card stat-card-online" shadow="hover">
            <div class="stat-inner">
              <div class="stat-icon-wrap">
                <el-icon class="stat-icon"><Connection /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.onlineCount }}</div>
                <div class="stat-label">在线人数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 快捷入口（直接来自权限树菜单，与侧栏菜单一致） -->
    <section v-if="quickLinkItems.length > 0" class="quick-section">
      <h2 class="section-title">快捷入口</h2>
      <el-row :gutter="16">
        <el-col v-for="item in quickLinkItems" :key="item.path" :xs="12" :sm="8" :md="6">
          <router-link :to="item.path" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <el-icon :size="28" class="quick-icon"><component :is="iconMap[item.iconName] ?? Document" /></el-icon>
              <span class="quick-label">{{ item.label }}</span>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </section>

    <!-- 最近操作日志 -->
    <section class="logs-section">
      <div class="section-header">
        <h2 class="section-title">最近操作</h2>
        <router-link
          v-if="authStore.hasPermission('admin:operation-logs')"
          to="/admin/operation-logs"
          class="more-link"
        >
          查看全部
        </router-link>
      </div>
      <el-card v-loading="logsLoading" shadow="never">
        <el-table :data="recentLogs" stripe style="width: 100%" max-height="320">
          <el-table-column prop="createTime" label="时间" width="170" show-overflow-tooltip />
          <el-table-column prop="username" label="操作人" width="100" />
          <el-table-column prop="operationDesc" label="操作描述" min-width="160" show-overflow-tooltip />
          <el-table-column prop="operationModule" label="模块" width="100" />
          <el-table-column prop="status" label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!logsLoading && recentLogs.length === 0" description="暂无操作记录" :image-size="80" />
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../../../stores/auth'
import {
  getUserListApi,
  getRoleListApi,
  getConsultationSessionListApi,
  getContentListApi,
  getOperationLogListApi,
  getOnlineCountApi,
  type OperationLog,
} from '../../../api/admin'
import { getAdminQuickLinkItemsFromTree } from '../../../utils/permission'
import {
  Clock,
  HomeFilled,
  UserFilled,
  Key,
  ChatLineRound,
  Reading,
  Connection,
  User,
  Setting,
  Folder,
  DataAnalysis,
  List,
  Document,
  DocumentCopy,
  Avatar,
  Message,
  ChatDotRound,
  Star,
  Picture,
  DocumentChecked,
  CollectionTag,
  PriceTag,
  TrendCharts,
  Search,
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
const currentTime = ref(new Date().toLocaleString('zh-CN'))

const stats = ref({
  userTotal: 0,
  roleTotal: 0,
  sessionTotal: 0,
  contentTotal: 0,
  onlineCount: 0,
})

const recentLogs = ref<OperationLog[]>([])
const logsLoading = ref(false)

/** 与 utils/permission 中 permissionToIconMap 的 value 对应，用于渲染图标 */
const iconMap: Record<string, unknown> = {
  HomeFilled,
  User,
  UserFilled: UserFilled,
  Key,
  Document,
  List,
  DocumentCopy,
  Avatar,
  Message,
  ChatDotRound,
  Star,
  Picture,
  DocumentChecked,
  CollectionTag,
  Folder,
  PriceTag,
  TrendCharts,
  Setting,
  Search,
  Reading,
  DataAnalysis,
}

/** 快捷入口：直接根据权限树的菜单权限（type=1）扁平化叶子节点生成，与侧栏菜单同源 */
const quickLinkItems = computed(() =>
  getAdminQuickLinkItemsFromTree(authStore.userInfo?.permissions ?? [])
)

let timer: number | null = null

const loadStats = async () => {
  try {
    const [userRes, roleRes, sessionRes, contentRes, onlineRes] = await Promise.all([
      getUserListApi({ current: 1, size: 1 }),
      getRoleListApi({ current: 1, size: 1 }),
      getConsultationSessionListApi({ current: 1, size: 1 }),
      getContentListApi({ current: 1, size: 1 }),
      getOnlineCountApi(),
    ])
    stats.value = {
      userTotal: userRes?.total ?? 0,
      roleTotal: roleRes?.total ?? 0,
      sessionTotal: sessionRes?.total ?? 0,
      contentTotal: contentRes?.total ?? 0,
      onlineCount: onlineRes?.onlineCount ?? 0,
    }
  } catch {
    // 静默失败，保持 0
  }
}

const loadRecentLogs = async () => {
  logsLoading.value = true
  try {
    const res = await getOperationLogListApi({ current: 1, size: 8 })
    recentLogs.value = res?.records ?? []
  } catch {
    recentLogs.value = []
  } finally {
    logsLoading.value = false
  }
}

onMounted(() => {
  loadStats()
  loadRecentLogs()
  timer = window.setInterval(() => {
    currentTime.value = new Date().toLocaleString('zh-CN')
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.admin-home {
  min-height: 100%;
  padding-bottom: 32px;
}

/* 欢迎横幅 */
.welcome-banner {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.welcome-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #2c5282 50%, #2b6cb0 100%);
  opacity: 0.96;
}

.welcome-content {
  position: relative;
  padding: 32px 36px;
  color: #fff;
}

.welcome-title {
  margin: 0 0 8px;
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.welcome-subtitle {
  margin: 0 0 16px;
  font-size: 0.875rem;
  opacity: 0.9;
}

.welcome-time {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9375rem;
  font-variant-numeric: tabular-nums;
}

.time-icon {
  font-size: 1.125rem;
  opacity: 0.9;
}

/* 区块标题 */
.section-title {
  margin: 0 0 16px;
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin: 0;
}

.more-link {
  font-size: 0.875rem;
  color: #409eff;
  text-decoration: none;
}

.more-link:hover {
  text-decoration: underline;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 28px;
}

.stat-card {
  transition: box-shadow 0.2s;
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  font-size: 26px;
  color: #fff;
}

.stat-card-user .stat-icon-wrap {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card-role .stat-icon-wrap {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card-consultation .stat-icon-wrap {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card-knowledge .stat-icon-wrap {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card-online .stat-icon-wrap {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  min-width: 0;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.8125rem;
  color: #6b7280;
  margin-top: 4px;
}

/* 快捷入口 */
.quick-section {
  margin-bottom: 28px;
}

.quick-link {
  text-decoration: none;
  color: inherit;
  display: block;
}

.quick-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.quick-card:hover {
  transform: translateY(-2px);
}

.quick-icon {
  color: #409eff;
  margin-bottom: 8px;
}

.quick-label {
  font-size: 0.875rem;
  color: #374151;
  display: block;
}

/* 最近操作 */
.logs-section {
  margin-bottom: 0;
}
</style>
