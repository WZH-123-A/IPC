<template>
  <div class="knowledge-view">
    <!-- Hero 区域 -->
    <section class="knowledge-hero">
      <div class="hero-bg" />
      <div class="hero-content">
        <h1 class="hero-title">病知识库</h1>
        <p class="hero-subtitle">权威健康科普，助您科学防病</p>
        <div class="hero-search">
          <el-input
            v-model="keyword"
            placeholder="搜索疾病、症状、护理知识..."
            clearable
            size="large"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </section>

    <!-- 主体：侧边分类 + 内容网格 -->
    <div class="knowledge-body">
      <aside class="knowledge-sidebar">
        <div class="sidebar-title">分类</div>
        <ul class="category-list">
          <li
            class="category-item"
            :class="{ active: !selectedCategoryId }"
            @click="selectCategory(null)"
          >
            <el-icon><Document /></el-icon>
            <span>全部</span>
          </li>
          <li
            v-for="cat in categories"
            :key="cat.id"
            class="category-item"
            :class="{ active: selectedCategoryId === cat.id }"
            @click="selectCategory(cat.id)"
          >
            <el-icon><Reading /></el-icon>
            <span>{{ cat.categoryName }}</span>
          </li>
        </ul>
      </aside>

      <main class="knowledge-main">
        <div v-loading="loading" class="content-grid">
          <template v-if="contents.length > 0">
            <article
              v-for="item in contents"
              :key="item.id"
              class="content-card"
              @click="openDetail(item)"
            >
              <div class="card-cover">
                <img
                  v-if="item.coverImage"
                  :src="resolveCoverUrl(item.coverImage)"
                  :alt="item.title"
                  loading="lazy"
                />
                <div v-else class="cover-placeholder">
                  <el-icon :size="48"><Document /></el-icon>
                </div>
                <span class="card-type-badge">{{ contentTypeLabel(item.contentType) }}</span>
              </div>
              <div class="card-body">
                <h3 class="card-title">{{ item.title }}</h3>
                <p v-if="item.subtitle" class="card-subtitle">{{ item.subtitle }}</p>
                <div class="card-meta">
                  <span v-if="item.source" class="meta-source">{{ item.source }}</span>
                  <span class="meta-views">
                    <el-icon><View /></el-icon>
                    {{ item.viewCount ?? 0 }}
                  </span>
                </div>
              </div>
            </article>
          </template>
          <el-empty v-else-if="!loading" description="暂无内容" :image-size="120" />
        </div>

        <div v-if="total > 0" class="pagination-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            background
            @current-change="loadContents"
            @size-change="loadContents"
          />
        </div>
      </main>
    </div>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="detailVisible"
      title=""
      size="560px"
      direction="rtl"
      class="detail-drawer"
      :with-header="false"
      destroy-on-close
      @closed="detailData = null"
    >
      <div v-if="detailData" class="detail-inner">
        <header class="detail-header">
          <span class="detail-type">{{ contentTypeLabel(detailData.contentType) }}</span>
          <h2 class="detail-title">{{ detailData.title }}</h2>
          <p v-if="detailData.subtitle" class="detail-subtitle">{{ detailData.subtitle }}</p>
          <div class="detail-meta">
            <span v-if="detailData.source">{{ detailData.source }}</span>
            <span v-if="detailData.author"> · {{ detailData.author }}</span>
            <span v-if="detailData.publishTime">
              · {{ formatDate(detailData.publishTime) }}
            </span>
            <span class="detail-views">
              <el-icon><View /></el-icon>
              {{ detailData.viewCount ?? 0 }} 次阅读
            </span>
          </div>
        </header>
        <div v-if="detailData.coverImage" class="detail-cover">
          <img :src="resolveCoverUrl(detailData.coverImage)" :alt="detailData.title" />
        </div>
        <div class="detail-content prose" v-html="detailData.content || ''" />
        <div v-if="detailData.videoUrl" class="detail-media">
          <video :src="resolveCoverUrl(detailData.videoUrl)" controls />
        </div>
        <div v-if="detailData.audioUrl" class="detail-media">
          <audio :src="resolveCoverUrl(detailData.audioUrl)" controls />
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Document, Reading, View } from '@element-plus/icons-vue'
import {
  getKnowledgeCategories,
  getKnowledgeContents,
  getKnowledgeContentDetail,
  type KnowledgeCategory,
  type KnowledgeContentItem,
  type KnowledgeContentDetail,
} from '../../api/patient/knowledge'

const categories = ref<KnowledgeCategory[]>([])
const contents = ref<KnowledgeContentItem[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const selectedCategoryId = ref<number | null>(null)

const detailVisible = ref(false)
const detailData = ref<KnowledgeContentDetail | null>(null)

function resolveCoverUrl(url?: string): string {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const origin =
    import.meta.env.DEV ? 'http://localhost:8080' : window.location.origin
  return `${origin}${url}`
}

function contentTypeLabel(type?: number): string {
  const map: Record<number, string> = {
    1: '图文',
    2: '视频',
    3: '音频',
    4: '文档',
  }
  return type != null ? map[type] ?? '图文' : '图文'
}

function formatDate(str?: string): string {
  if (!str) return ''
  const d = new Date(str)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const loadCategories = async () => {
  try {
    const res = await getKnowledgeCategories()
    categories.value = res.data ?? []
  } catch {
    categories.value = []
  }
}

const loadContents = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeContents({
      current: currentPage.value,
      size: pageSize.value,
      categoryId: selectedCategoryId.value ?? undefined,
      keyword: keyword.value.trim() || undefined,
    })
    const data = res.data
    contents.value = data?.records ?? []
    total.value = data?.total ?? 0
  } catch {
    contents.value = []
    total.value = 0
    ElMessage.error('加载内容失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadContents()
}

const selectCategory = (id: number | null) => {
  selectedCategoryId.value = id
  currentPage.value = 1
  loadContents()
}

const openDetail = async (item: KnowledgeContentItem) => {
  try {
    const res = await getKnowledgeContentDetail(item.id)
    detailData.value = res.data ?? null
    detailVisible.value = true
  } catch {
    ElMessage.error('加载详情失败')
  }
}

watch([selectedCategoryId, keyword], () => {
  currentPage.value = 1
})

onMounted(() => {
  loadCategories()
  loadContents()
})
</script>

<style scoped>
.knowledge-view {
  min-height: 100%;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 12%, #fff 24%);
}

/* Hero */
.knowledge-hero {
  position: relative;
  padding: 48px 24px 40px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  opacity: 0.96;
}

.hero-content {
  position: relative;
  max-width: 720px;
  margin: 0 auto;
  text-align: center;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 2.25rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #fff;
  font-family: 'Georgia', 'Songti SC', serif;
}

.hero-subtitle {
  margin: 0 0 32px;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.85);
}

.hero-search {
  max-width: 480px;
  margin: 0 auto;
}

.search-input {
  --el-input-border-radius: 9999px;
  --el-input-bg-color: rgba(255, 255, 255, 0.95);
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 9999px 9999px 0;
  padding: 0 4px 0 0;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 9999px 0 0 9999px;
  box-shadow: none;
}

/* Body */
.knowledge-body {
  display: flex;
  max-width: 1280px;
  margin: 0 auto;
  padding: 24px;
  gap: 32px;
}

.knowledge-sidebar {
  flex-shrink: 0;
  width: 200px;
}

.sidebar-title {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #64748b;
  margin-bottom: 12px;
}

.category-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 0.9375rem;
  color: #475569;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.category-item:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.category-item.active {
  background: #0f172a;
  color: #fff;
}

.category-item .el-icon {
  font-size: 1.125rem;
  opacity: 0.9;
}

.knowledge-main {
  flex: 1;
  min-width: 0;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  min-height: 200px;
}

.content-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.content-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
}

.card-cover {
  position: relative;
  aspect-ratio: 16 / 10;
  background: #f1f5f9;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}

.card-type-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #fff;
  background: rgba(15, 23, 42, 0.7);
  border-radius: 6px;
}

.card-body {
  padding: 18px;
}

.card-title {
  margin: 0 0 8px;
  font-size: 1.0625rem;
  font-weight: 600;
  line-height: 1.4;
  color: #0f172a;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-subtitle {
  margin: 0 0 12px;
  font-size: 0.8125rem;
  color: #64748b;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.75rem;
  color: #94a3b8;
}

.meta-source {
  color: #64748b;
}

.meta-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* Detail drawer */
.detail-drawer :deep(.el-drawer__body) {
  padding: 0;
  overflow-y: auto;
  background: #fafafa;
}

.detail-inner {
  max-width: 520px;
  margin: 0 auto;
  padding: 32px 24px 48px;
  background: #fff;
  min-height: 100%;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-type {
  display: inline-block;
  padding: 4px 10px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #0f172a;
  background: #e2e8f0;
  border-radius: 6px;
  margin-bottom: 12px;
}

.detail-title {
  margin: 0 0 8px;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1.35;
  color: #0f172a;
  font-family: 'Georgia', 'Songti SC', serif;
}

.detail-subtitle {
  margin: 0 0 12px;
  font-size: 0.9375rem;
  color: #64748b;
  line-height: 1.5;
}

.detail-meta {
  font-size: 0.8125rem;
  color: #94a3b8;
}

.detail-views {
  margin-left: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.detail-cover {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  background: #f1f5f9;
}

.detail-cover img {
  width: 100%;
  display: block;
}

.detail-content.prose {
  font-size: 1rem;
  line-height: 1.75;
  color: #334155;
}

.detail-content.prose :deep(p) {
  margin: 0 0 1em;
}

.detail-content.prose :deep(h2),
.detail-content.prose :deep(h3) {
  margin: 1.5em 0 0.5em;
  font-weight: 600;
  color: #0f172a;
}

.detail-content.prose :deep(ul),
.detail-content.prose :deep(ol) {
  margin: 0 0 1em;
  padding-left: 1.5em;
}

.detail-content.prose :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.detail-media {
  margin-top: 24px;
}

.detail-media video,
.detail-media audio {
  width: 100%;
  border-radius: 8px;
}
</style>
