<template>
  <div class="consultation-header">
    <div class="header-left">
      <h2 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        <span>在线问诊</span>
      </h2>
      <p class="page-subtitle">与AI或专业医生进行在线咨询，获得专业的医疗建议</p>
    </div>
    <div class="header-right">
      <el-button
        v-permission="'patient:consultation:create'"
        type="primary"
        size="large"
        @click="$emit('create')"
      >
        <el-icon><Plus /></el-icon>
        <span>新建问诊</span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChatDotRound, Plus } from '@element-plus/icons-vue'

defineEmits<{
  create: []
}>()
</script>

<style scoped>
.consultation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  flex: 1;
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
</style>

