<template>
  <div class="patient-home">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ authStore.userInfo?.realName || authStore.userInfo?.username }}！</h2>
          <p class="subtitle">您的健康是我们最关心的事</p>
        </div>
        <div class="welcome-illustration">
          <el-icon class="icon" :size="120"><ChatDotRound /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 快捷功能卡片 -->
    <div class="quick-actions">
      <el-card
        v-if="authStore.hasPermission('patient:consultation')"
        class="action-card"
        shadow="hover"
        @click="goToConsultation"
      >
        <div class="action-content">
          <div class="action-icon consultation">
            <el-icon :size="48"><ChatDotRound /></el-icon>
          </div>
          <h3>在线问诊</h3>
          <p>与AI或专业医生进行在线咨询</p>
        </div>
      </el-card>

      <el-card
        v-if="authStore.hasPermission('patient:diagnosis')"
        class="action-card"
        shadow="hover"
        @click="goToDiagnosis"
      >
        <div class="action-content">
          <div class="action-icon diagnosis">
            <el-icon :size="48"><Search /></el-icon>
          </div>
          <h3>皮肤诊断</h3>
          <p>上传图片，AI智能识别皮肤问题</p>
        </div>
      </el-card>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-card class="stats-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>我的数据</span>
          </div>
        </template>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ consultationCount }}</div>
            <div class="stat-label">问诊记录</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ diagnosisCount }}</div>
            <div class="stat-label">诊断记录</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ activeConsultations }}</div>
            <div class="stat-label">进行中问诊</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 最近记录 -->
    <div class="recent-section">
      <el-card class="recent-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Clock /></el-icon>
            <span>最近问诊</span>
          </div>
        </template>
        <div v-if="recentConsultations.length === 0" class="empty-state">
          <el-empty description="暂无问诊记录" :image-size="100">
            <el-button v-permission="'patient:consultation:create'" type="primary" @click="goToConsultation">开始问诊</el-button>
          </el-empty>
        </div>
        <div v-else class="consultation-list">
          <div
            v-for="item in recentConsultations"
            :key="item.id"
            class="consultation-item"
            @click="viewConsultation(item.id)"
          >
            <div class="item-left">
              <el-icon class="item-icon" :class="item.sessionType === 1 ? 'ai' : 'doctor'">
                <component :is="item.sessionType === 1 ? 'ChatDotRound' : 'UserFilled'" />
              </el-icon>
              <div class="item-info">
                <div class="item-title">{{ item.title || '未命名问诊' }}</div>
                <div class="item-meta">
                  <span>{{ item.sessionType === 1 ? 'AI问诊' : '医生问诊' }}</span>
                  <el-divider direction="vertical" />
                  <span>{{ formatTime(item.createTime) }}</span>
                </div>
              </div>
            </div>
            <div class="item-right">
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ChatDotRound, Search, DataAnalysis, Clock } from '@element-plus/icons-vue'
import { getConsultationSessions } from '../../api/patient/consultation'
import type { ConsultationSession } from '../../api/patient/consultation'

const router = useRouter()
const authStore = useAuthStore()

const consultationCount = ref(0)
const diagnosisCount = ref(0)
const activeConsultations = ref(0)
const recentConsultations = ref<ConsultationSession[]>([])

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  try {
    // 加载问诊记录
    const response = await getConsultationSessions({ current: 1, size: 10 })
    if (response.data) {
      recentConsultations.value = response.data.records || []
      consultationCount.value = response.data.total || 0
      activeConsultations.value = recentConsultations.value.filter(
        (item) => item.status === 0,
      ).length
    }

    // 加载诊断记录统计
    const { getDiagnosisRecords } = await import('../../api/patient/diagnosis')
    const diagnosisResponse = await getDiagnosisRecords({ current: 1, size: 1 })
    if (diagnosisResponse.data) {
      diagnosisCount.value = diagnosisResponse.data.total || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const goToConsultation = () => {
  router.push('/patient/consultation')
}

const goToDiagnosis = () => {
  router.push('/patient/diagnosis')
}

const viewConsultation = (id: number) => {
  router.push({
    path: '/patient/consultation',
    query: { sessionId: id },
  })
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info',
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '进行中',
    1: '已结束',
    2: '已取消',
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.patient-home {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-card {
  border-radius: 16px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.welcome-card :deep(.el-card__body) {
  padding: 0;
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px;
  color: #fff;
}

.welcome-text h2 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 700;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-illustration {
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-illustration .icon {
  opacity: 0.3;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.action-card {
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.action-card:hover {
  transform: translateY(-4px);
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.action-content {
  text-align: center;
  padding: 20px;
}

.action-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #fff;
}

.action-icon.consultation {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-icon.diagnosis {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.action-content h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.action-content p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-section {
  margin-top: 8px;
}

.stats-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.recent-section {
  margin-top: 8px;
}

.recent-card {
  border-radius: 16px;
}

.empty-state {
  padding: 40px 0;
}

.consultation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consultation-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 12px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.consultation-item:hover {
  background: #eef0f3;
  transform: translateX(4px);
}

.item-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.item-icon {
  font-size: 32px;
  color: #667eea;
}

.item-icon.ai {
  color: #764ba2;
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
}

.item-right {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    padding: 30px 20px;
  }

  .welcome-text h2 {
    font-size: 24px;
  }

  .welcome-illustration {
    margin-top: 20px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
