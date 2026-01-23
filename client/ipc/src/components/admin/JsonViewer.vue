<template>
  <div class="json-viewer">
    <pre v-if="formattedJson">{{ formattedJson }}</pre>
    <span v-else>-</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  json?: string | null
}

const props = defineProps<Props>()

const formattedJson = computed(() => {
  if (!props.json) return null
  try {
    const obj = JSON.parse(props.json)
    return JSON.stringify(obj, null, 2)
  } catch {
    return props.json
  }
})
</script>

<style scoped>
.json-viewer {
  max-height: 300px;
  overflow: auto;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
}

.json-viewer pre {
  margin: 0;
  padding: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: #606266;
}

/* 滚动条样式 */
.json-viewer::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.json-viewer::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.json-viewer::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.json-viewer::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>

