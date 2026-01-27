<template>
  <div class="diagnosis-view">
    <!-- 顶部标题 -->
    <div class="diagnosis-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon><Search /></el-icon>
          <span>皮肤诊断</span>
        </h2>
        <p class="page-subtitle">上传皮肤图片，AI智能识别皮肤问题，获得专业的诊断建议</p>
      </div>
    </div>

    <div class="diagnosis-container">
      <!-- 左侧上传区域 -->
      <div class="upload-panel">
        <el-card shadow="hover" class="upload-card">
          <template #header>
            <div class="card-header">
              <el-icon><Upload /></el-icon>
              <span>上传图片</span>
            </div>
          </template>
          <div class="upload-content">
            <el-upload
              ref="uploadRef"
              class="upload-dragger"
              drag
              :auto-upload="false"
              :on-change="handleImageChange"
              :show-file-list="false"
              accept="image/*"
            >
              <div class="upload-area">
                <el-icon class="upload-icon"><Picture /></el-icon>
                <div class="upload-text">
                  <p class="upload-title">点击或拖拽图片到此处上传</p>
                  <p class="upload-hint">支持 JPG、PNG 格式，建议图片清晰</p>
                </div>
              </div>
            </el-upload>

            <div v-if="selectedImage" class="image-preview">
              <div class="preview-header">
                <span>预览图片</span>
                <el-button text type="danger" @click="clearImage">
                  <el-icon><Delete /></el-icon>
                  清除
                </el-button>
              </div>
              <div class="preview-image">
                <el-image :src="previewUrl" fit="cover" />
              </div>
              <div class="preview-actions">
                <el-select
                  v-model="bodyPart"
                  placeholder="选择身体部位（可选）"
                  clearable
                  style="width: 100%"
                >
                  <el-option label="面部" value="面部" />
                  <el-option label="手臂" value="手臂" />
                  <el-option label="腿部" value="腿部" />
                  <el-option label="背部" value="背部" />
                  <el-option label="胸部" value="胸部" />
                  <el-option label="其他" value="其他" />
                </el-select>
                <el-button
                  type="primary"
                  size="large"
                  :loading="uploading"
                  @click="uploadAndDiagnose"
                  style="width: 100%; margin-top: 12px"
                >
                  <el-icon><Search /></el-icon>
                  <span>开始诊断</span>
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 诊断记录列表 -->
        <el-card shadow="hover" class="records-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>诊断记录</span>
            </div>
          </template>
          <div class="records-list">
            <div
              v-for="record in records"
              :key="record.id"
              class="record-item"
              :class="{ active: currentRecordId === record.id }"
              @click="selectRecord(record.id)"
            >
              <div class="record-image">
                <el-image :src="record.imageUrl" fit="cover" />
                <div class="record-status">
                  <el-tag :type="getRecordStatusType(record.status)" size="small">
                    {{ getRecordStatusText(record.status) }}
                  </el-tag>
                </div>
              </div>
              <div class="record-info">
                <div class="record-time">{{ formatTime(record.createTime) }}</div>
                <div v-if="record.bodyPart" class="record-body-part">
                  <el-icon><Location /></el-icon>
                  {{ record.bodyPart }}
                </div>
              </div>
            </div>
            <div v-if="records.length === 0" class="empty-records">
              <el-empty description="暂无诊断记录" :image-size="100" />
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧结果展示区域 -->
      <div class="result-panel">
        <div v-if="!currentRecordId" class="empty-result">
          <el-empty description="请上传图片进行诊断或选择历史记录查看" :image-size="200">
            <el-button type="primary" size="large" @click="scrollToUpload">
              <el-icon><Upload /></el-icon>
              <span>上传图片</span>
            </el-button>
          </el-empty>
        </div>
        <div v-else class="result-content">
          <!-- 结果头部 -->
          <div class="result-header">
            <h3>诊断结果</h3>
            <div class="result-meta">
              <el-tag :type="getRecordStatusType(currentRecord?.status || 0)">
                {{ getRecordStatusText(currentRecord?.status || 0) }}
              </el-tag>
              <span class="result-time">{{ formatTime(currentRecord?.createTime || '') }}</span>
            </div>
          </div>

          <!-- 原图展示 -->
          <div class="original-image-section">
            <h4>原始图片</h4>
            <div class="image-display">
              <el-image
                :src="currentRecord?.imageUrl"
                fit="contain"
                :preview-src-list="[currentRecord?.imageUrl]"
              />
            </div>
            <div v-if="currentRecord?.bodyPart" class="image-info">
              <el-icon><Location /></el-icon>
              <span>身体部位：{{ currentRecord.bodyPart }}</span>
            </div>
          </div>

          <!-- 诊断结果 -->
          <div v-if="diagnosisResults.length > 0" class="results-section">
            <h4>AI诊断结果</h4>
            <div class="results-list">
              <div
                v-for="(result, index) in diagnosisResults"
                :key="result.id"
                class="result-item"
                :class="{ 'result-top': index === 0 }"
              >
                <div class="result-rank">
                  <div class="rank-badge" :class="{ 'rank-first': index === 0 }">
                    {{ index + 1 }}
                  </div>
                </div>
                <div class="result-details">
                  <div class="result-disease">
                    <span class="disease-name">{{ result.diseaseName || '未知疾病' }}</span>
                    <el-tag :type="index === 0 ? 'success' : 'info'" size="small">
                      {{ result.confidence.toFixed(1) }}%
                    </el-tag>
                  </div>
                  <div class="result-confidence">
                    <div class="confidence-bar">
                      <div
                        class="confidence-fill"
                        :style="{ width: `${result.confidence}%` }"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="currentRecord?.status === 0" class="diagnosing">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>AI正在分析中，请稍候...</span>
          </div>
          <div v-else class="no-results">
            <el-alert type="warning" :closable="false"> 诊断失败，请重新上传图片 </el-alert>
          </div>

          <!-- 诊断建议 -->
          <div v-if="diagnosisResults.length > 0" class="suggestions-section">
            <h4>诊断建议</h4>
            <div class="suggestions-content">
              <el-alert type="info" :closable="false">
                <template #title>
                  <div class="suggestion-title">温馨提示</div>
                </template>
                <div class="suggestion-text">
                  <p>• 本诊断结果仅供参考，不能替代专业医生的诊断和治疗建议。</p>
                  <p>• 如果症状持续或加重，请及时就医，咨询专业医生。</p>
                  <p>• 建议结合医生的专业意见进行综合判断和治疗。</p>
                </div>
              </el-alert>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Upload,
  Picture,
  Delete,
  Document,
  Location,
  Loading,
} from '@element-plus/icons-vue'
import {
  uploadDiagnosisImage,
  getDiagnosisRecords,
  getDiagnosisRecord,
  getDiagnosisResults,
  type DiagnosisRecord,
  type DiagnosisResult,
} from '../../api/patient/diagnosis'
import type { UploadFile } from 'element-plus'

const uploadRef = ref()
const selectedImage = ref<File | null>(null)
const previewUrl = ref('')
const bodyPart = ref('')
const uploading = ref(false)
const records = ref<DiagnosisRecord[]>([])
const currentRecordId = ref<number | null>(null)
const currentRecord = ref<DiagnosisRecord | null>(null)
const diagnosisResults = ref<DiagnosisResult[]>([])

onMounted(async () => {
  await loadRecords()
})

const handleImageChange = (file: UploadFile) => {
  selectedImage.value = file.raw || null
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(file.raw)
  }
}

const clearImage = () => {
  selectedImage.value = null
  previewUrl.value = ''
  bodyPart.value = ''
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const uploadAndDiagnose = async () => {
  if (!selectedImage.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  uploading.value = true
  try {
    const response = await uploadDiagnosisImage({
      image: selectedImage.value,
      bodyPart: bodyPart.value || undefined,
    })
    if (response.data) {
      ElMessage.success('诊断完成')
      clearImage()
      await loadRecords()
      selectRecord(response.data.id)
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '诊断失败'
    ElMessage.error(message)
  } finally {
    uploading.value = false
  }
}

const loadRecords = async () => {
  try {
    const response = await getDiagnosisRecords({ current: 1, size: 50 })
    if (response.data) {
      records.value = response.data.records || []
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载记录失败'
    ElMessage.error(message)
  }
}

const selectRecord = async (recordId: number) => {
  currentRecordId.value = recordId
  try {
    // 加载记录详情
    const recordResponse = await getDiagnosisRecord(recordId)
    if (recordResponse.data) {
      currentRecord.value = recordResponse.data
    }

    // 加载诊断结果
    if (recordResponse.data?.status === 1) {
      const resultsResponse = await getDiagnosisResults(recordId)
      if (resultsResponse.data) {
        diagnosisResults.value = resultsResponse.data
      }
    } else {
      diagnosisResults.value = []
      // 如果正在识别中，轮询检查结果
      if (recordResponse.data?.status === 0) {
        pollDiagnosisResults(recordId)
      }
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '加载记录详情失败'
    ElMessage.error(message)
  }
}

const pollDiagnosisResults = async (recordId: number) => {
  const maxAttempts = 30
  let attempts = 0
  const interval = setInterval(async () => {
    attempts++
    try {
      const recordResponse = await getDiagnosisRecord(recordId)
      if (recordResponse.data?.status === 1) {
        clearInterval(interval)
        const resultsResponse = await getDiagnosisResults(recordId)
        if (resultsResponse.data) {
          diagnosisResults.value = resultsResponse.data
        }
      } else if (attempts >= maxAttempts || recordResponse.data?.status === 2) {
        clearInterval(interval)
      }
    } catch {
      if (attempts >= maxAttempts) {
        clearInterval(interval)
      }
    }
  }, 2000)
}

const scrollToUpload = () => {
  const uploadCard = document.querySelector('.upload-card')
  if (uploadCard) {
    uploadCard.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const getRecordStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  }
  return map[status] || 'info'
}

const getRecordStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '识别中',
    1: '识别成功',
    2: '识别失败',
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.diagnosis-view {
  min-height: calc(100vh - 120px);
  background: #f5f7fa;
}

.diagnosis-header {
  margin-bottom: 16px;
}

.header-content {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.page-title .el-icon {
  color: #667eea;
}

.page-subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.diagnosis-container {
  display: flex;
  gap: 16px;
}

.upload-panel {
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.upload-card,
.records-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.upload-content {
  padding: 20px;
}

.upload-dragger {
  width: 100%;
}

.upload-area {
  padding: 40px;
  text-align: center;
}

.upload-icon {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.upload-hint {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.image-preview {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #303133;
}

.preview-image {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
}

.preview-image .el-image {
  width: 100%;
  height: 100%;
}

.records-list {
  max-height: 500px;
  overflow-y: auto;
  padding: 8px;
}

.record-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.record-item:hover {
  background: #f5f7fa;
}

.record-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border: 1px solid #667eea;
}

.record-image {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.record-image .el-image {
  width: 100%;
  height: 100%;
}

.record-status {
  position: absolute;
  top: 4px;
  right: 4px;
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.record-time {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.record-body-part {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.empty-records {
  padding: 40px 20px;
  text-align: center;
}

.result-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.empty-result {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;
}

.result-content {
  padding: 24px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.result-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-time {
  font-size: 14px;
  color: #909399;
}

.original-image-section {
  margin-bottom: 32px;
}

.original-image-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.image-display {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  background: #f5f7fa;
}

.image-display .el-image {
  width: 100%;
  height: 100%;
}

.image-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #909399;
}

.results-section {
  margin-bottom: 32px;
}

.results-section h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.result-item.result-top {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border: 2px solid #667eea;
}

.result-rank {
  flex-shrink: 0;
}

.rank-badge {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: #909399;
  border: 2px solid #ebeef5;
}

.rank-badge.rank-first {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-color: #667eea;
}

.result-details {
  flex: 1;
}

.result-disease {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.disease-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.result-confidence {
  margin-top: 8px;
}

.confidence-bar {
  width: 100%;
  height: 8px;
  background: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.confidence-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.diagnosing,
.no-results {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #909399;
}

.suggestions-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.suggestions-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.suggestion-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.suggestion-text {
  line-height: 1.8;
}

.suggestion-text p {
  margin: 8px 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .diagnosis-container {
    flex-direction: column;
  }

  .upload-panel {
    width: 100%;
  }

  .records-list {
    max-height: 300px;
  }
}
</style>
