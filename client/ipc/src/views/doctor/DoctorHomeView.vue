<template>
  <div class="doctor-home">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>医生首页</span>
        </div>
      </template>

      <!-- 欢迎信息 -->
      <div class="welcome-section">
        <h2>
          欢迎，{{
            doctorHome?.doctorInfo?.realName ||
            authStore.userInfo?.realName ||
            authStore.userInfo?.username
          }}
          医生
        </h2>
        <p v-if="doctorHome?.doctorInfo">
          {{ doctorHome.doctorInfo.hospital }} - {{ doctorHome.doctorInfo.department }} -
          {{ doctorHome.doctorInfo.title }}
        </p>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409eff">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ doctorHome?.todayConsultationCount || 0 }}</div>
                <div class="stat-label">今日问诊</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67c23a">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ doctorHome?.ongoingConsultationCount || 0 }}</div>
                <div class="stat-label">进行中</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #e6a23c">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ doctorHome?.totalPatientCount || 0 }}</div>
                <div class="stat-label">总患者数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #f56c6c">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ doctorHome?.monthConsultationCount || 0 }}</div>
                <div class="stat-label">本月问诊</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 医生信息 -->
      <el-card v-if="doctorHome?.doctorInfo" class="info-card">
        <template #header>
          <span>医生信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{
            doctorHome.doctorInfo.username
          }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{
            doctorHome.doctorInfo.realName || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{
            doctorHome.doctorInfo.phone || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{
            doctorHome.doctorInfo.email || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="所属医院">{{
            doctorHome.doctorInfo.hospital || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{
            doctorHome.doctorInfo.department || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{
            doctorHome.doctorInfo.title || '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="从业年限">{{
            doctorHome.doctorInfo.workYears ? `${doctorHome.doctorInfo.workYears}年` : '-'
          }}</el-descriptions-item>
          <el-descriptions-item label="擅长领域" :span="2">{{
            doctorHome.doctorInfo.specialty || '-'
          }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { getDoctorHomeApi, type DoctorHome } from '../../api/doctor'
import { ElMessage } from 'element-plus'
import { Calendar, Clock, User, Document } from '@element-plus/icons-vue'

const authStore = useAuthStore()
const doctorHome = ref<DoctorHome | null>(null)
const loading = ref(false)

const loadData = async () => {
  try {
    loading.value = true
    doctorHome.value = await getDoctorHomeApi()
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载数据失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.doctor-home {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.welcome-section h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.welcome-section p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.info-card {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>
