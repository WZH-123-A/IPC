<template>
  <div class="daily-manager">
    <!-- 页面标题区 -->
    <section class="page-header">
      <div class="header-bg" />
      <div class="header-content">
        <h1 class="page-title">每日统计</h1>
        <p class="page-subtitle">按日期与类型查看平台运营数据汇总</p>
      </div>
    </section>

    <!-- 汇总卡片 -->
    <section class="summary-section">
      <h2 class="section-title">数据汇总</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="summary-card summary-card-total" shadow="hover">
            <div class="summary-inner">
              <div class="summary-icon-wrap">
                <el-icon class="summary-icon"><DataLine /></el-icon>
              </div>
              <div class="summary-info">
                <div class="summary-value">{{ summary.totalRecords }}</div>
                <div class="summary-label">总记录数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="summary-card summary-card-user" shadow="hover">
            <div class="summary-inner">
              <div class="summary-icon-wrap">
                <el-icon class="summary-icon"><UserFilled /></el-icon>
              </div>
              <div class="summary-info">
                <div class="summary-value">{{ sumByTypeLabel('user_register') }}</div>
                <div class="summary-label">用户注册</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="summary-card summary-card-consult" shadow="hover">
            <div class="summary-inner">
              <div class="summary-icon-wrap">
                <el-icon class="summary-icon"><ChatLineRound /></el-icon>
              </div>
              <div class="summary-info">
                <div class="summary-value">{{ sumByTypeLabel('consultation') }}</div>
                <div class="summary-label">问诊量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-card class="summary-card summary-card-knowledge" shadow="hover">
            <div class="summary-inner">
              <div class="summary-icon-wrap">
                <el-icon class="summary-icon"><Reading /></el-icon>
              </div>
              <div class="summary-info">
                <div class="summary-value">{{ sumByTypeLabel('knowledge_view') }}</div>
                <div class="summary-label">知识浏览</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 筛选与按类型分布 -->
    <section class="filter-section">
      <el-card shadow="never">
        <div class="filter-row">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                :shortcuts="dateShortcuts"
                clearable
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item label="统计类型">
              <el-select
                v-model="filterForm.statType"
                placeholder="全部类型"
                clearable
                style="width: 160px"
              >
                <el-option
                  v-for="(label, key) in statTypeOptions"
                  :key="key"
                  :label="label"
                  :value="key"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="collect-row">
          <span class="collect-label">触发统计：</span>
          <el-button
            type="success"
            :loading="collectLoading"
            @click="handleCollectYesterday"
          >
            采集昨日
          </el-button>
          <el-date-picker
            v-model="collectDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 160px; margin-left: 12px"
          />
          <el-button
            type="success"
            plain
            :loading="collectLoading"
            :disabled="!collectDate"
            @click="handleCollectDate"
          >
            采集该日
          </el-button>
        </div>

        <!-- 按类型分布条 -->
        <div v-if="Object.keys(summary.sumByType || {}).length > 0" class="distribution-block">
          <h3 class="distribution-title">按类型分布</h3>
          <div class="distribution-bars">
            <div
              v-for="(value, type) in summary.sumByType"
              :key="type"
              class="distribution-item"
            >
              <span class="distribution-label">{{ statTypeOptions[type] ?? type }}</span>
              <div class="distribution-bar-wrap">
                <div
                  class="distribution-bar"
                  :style="{ width: barWidthPercent(type) + '%' }"
                />
                <span class="distribution-value">{{ value }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </section>

    <!-- 数据表格 -->
    <section class="table-section">
      <el-card shadow="never">
        <template #header>
          <span>明细列表</span>
        </template>
        <el-table
          v-loading="loading"
          :data="dataList"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="statDate" label="统计日期" width="120" align="center" />
          <el-table-column prop="statType" label="统计类型" width="140">
            <template #default="{ row }">
              {{ statTypeOptions[row.statType] ?? row.statType }}
            </template>
          </el-table-column>
          <el-table-column prop="statValue" label="统计值" width="100" align="right" />
          <el-table-column prop="extraData" label="扩展数据" min-width="120" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip />
        </el-table>
        <AdminPagination
          :current="pagination.current"
          :size="pagination.size"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
        <el-empty
          v-if="!loading && dataList.length === 0"
          description="暂无数据"
          :image-size="80"
        />
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { DataLine, UserFilled, ChatLineRound, Reading } from '@element-plus/icons-vue'
import AdminPagination from '../../../components/admin/AdminPagination.vue'
import {
  getStatisticsDailyListApi,
  getStatisticsDailySummaryApi,
  collectStatisticsDailyApi,
  type StatisticsDailyItem,
  type StatisticsDailySummaryResult,
} from '../../../api/admin'

const statTypeOptions: Record<string, string> = {
  user_register: '用户注册',
  consultation: '问诊',
  diagnosis: '诊断',
  knowledge_view: '知识浏览',
}

const dateShortcuts = [
  { text: '最近 7 天', value: () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
    return [start, end]
  } },
  { text: '最近 30 天', value: () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
    return [start, end]
  } },
  { text: '本月', value: () => {
    const d = new Date()
    const start = new Date(d.getFullYear(), d.getMonth(), 1)
    const end = new Date(d.getFullYear(), d.getMonth() + 1, 0)
    return [start, end]
  } },
]

const filterForm = reactive<{
  dateRange: [string, string] | null
  statType: string
}>({
  dateRange: null,
  statType: '',
})

const collectDate = ref<string | null>(null)
const collectLoading = ref(false)

const summary = ref<StatisticsDailySummaryResult>({
  totalRecords: 0,
  sumByType: {},
})
const dataList = ref<StatisticsDailyItem[]>([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const maxBarValue = computed(() => {
  const byType = summary.value.sumByType
  if (!byType || Object.keys(byType).length === 0) return 1
  return Math.max(...Object.values(byType), 1)
})

function sumByTypeLabel(type: string): number {
  return summary.value.sumByType?.[type] ?? 0
}

function barWidthPercent(type: string): number {
  const v = summary.value.sumByType?.[type] ?? 0
  if (maxBarValue.value <= 0) return 0
  return Math.min(100, (v / maxBarValue.value) * 100)
}

function buildListParams() {
  const [startDate, endDate] = filterForm.dateRange ?? [undefined, undefined]
  return {
    current: pagination.current,
    size: pagination.size,
    startDate: startDate ?? undefined,
    endDate: endDate ?? undefined,
    statType: filterForm.statType || undefined,
  }
}

async function loadSummary() {
  try {
    const [startDate, endDate] = filterForm.dateRange ?? [undefined, undefined]
    const res = await getStatisticsDailySummaryApi({
      startDate: startDate ?? undefined,
      endDate: endDate ?? undefined,
      statType: filterForm.statType || undefined,
    })
    summary.value = res ?? { totalRecords: 0, sumByType: {} }
  } catch {
    summary.value = { totalRecords: 0, sumByType: {} }
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = buildListParams()
    const res = await getStatisticsDailyListApi(params)
    dataList.value = res?.records ?? []
    pagination.total = res?.total ?? 0
  } catch {
    dataList.value = []
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  loadSummary()
  loadList()
}

function handleReset() {
  filterForm.dateRange = null
  filterForm.statType = ''
  pagination.current = 1
  loadSummary()
  loadList()
}

async function handleCollectYesterday() {
  collectLoading.value = true
  try {
    await collectStatisticsDailyApi()
    ElMessage.success('昨日统计已采集')
    loadSummary()
    loadList()
  } catch {
    ElMessage.error('采集失败')
  } finally {
    collectLoading.value = false
  }
}

async function handleCollectDate() {
  if (!collectDate.value) return
  collectLoading.value = true
  try {
    await collectStatisticsDailyApi(collectDate.value)
    ElMessage.success(`已采集 ${collectDate.value} 的统计数据`)
    loadSummary()
    loadList()
  } catch {
    ElMessage.error('采集失败')
  } finally {
    collectLoading.value = false
  }
}

function handleSizeChange(size: number) {
  pagination.size = size
  pagination.current = 1
  loadList()
}

function handleCurrentChange(current: number) {
  pagination.current = current
  loadList()
}

onMounted(() => {
  loadSummary()
  loadList()
})
</script>

<style scoped>
.daily-manager {
  min-height: 100%;
  padding-bottom: 32px;
}

/* 页面标题 */
.page-header {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.header-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f766e 0%, #0d9488 50%, #14b8a6 100%);
  opacity: 0.96;
}

.header-content {
  position: relative;
  padding: 28px 32px;
  color: #fff;
}

.page-title {
  margin: 0 0 8px;
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.page-subtitle {
  margin: 0;
  font-size: 0.875rem;
  opacity: 0.92;
}

/* 区块标题 */
.section-title {
  margin: 0 0 16px;
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
}

/* 汇总卡片 */
.summary-section {
  margin-bottom: 24px;
}

.summary-card {
  transition: box-shadow 0.2s;
}

.summary-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.summary-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.summary-icon {
  font-size: 24px;
  color: #fff;
}

.summary-card-total .summary-icon-wrap {
  background: linear-gradient(135deg, #0d9488 0%, #14b8a6 100%);
}

.summary-card-user .summary-icon-wrap {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
}

.summary-card-consult .summary-icon-wrap {
  background: linear-gradient(135deg, #0ea5e9 0%, #38bdf8 100%);
}

.summary-card-knowledge .summary-icon-wrap {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
}

.summary-info {
  min-width: 0;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.summary-label {
  font-size: 0.8125rem;
  color: #6b7280;
  margin-top: 4px;
}

/* 筛选与分布 */
.filter-section {
  margin-bottom: 24px;
}

.filter-row {
  margin-bottom: 0;
}

.filter-form {
  margin-bottom: 0;
}

.collect-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.collect-label {
  font-size: 0.875rem;
  color: #4b5563;
  margin-right: 4px;
}

.distribution-block {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.distribution-title {
  margin: 0 0 16px;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #374151;
}

.distribution-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.distribution-label {
  width: 100px;
  font-size: 0.875rem;
  color: #4b5563;
  flex-shrink: 0;
}

.distribution-bar-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.distribution-bar {
  height: 24px;
  border-radius: 6px;
  background: linear-gradient(90deg, #0d9488 0%, #14b8a6 100%);
  min-width: 4px;
  transition: width 0.3s ease;
}

.distribution-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1f2937;
  min-width: 48px;
  text-align: right;
}

/* 表格区 */
.table-section {
  margin-bottom: 0;
}

.table-section :deep(.el-card__header) {
  font-weight: 600;
  color: #1f2937;
}
</style>
