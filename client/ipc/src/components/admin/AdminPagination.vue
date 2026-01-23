<template>
  <div class="pagination-wrapper">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="pageSizes"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  current: number
  size: number
  total: number
  pageSizes?: number[]
}

interface Emits {
  (e: 'size-change', size: number): void
  (e: 'current-change', current: number): void
}

const props = withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 50, 100],
})

const emit = defineEmits<Emits>()

const currentPage = computed({
  get: () => props.current,
  set: (value) => emit('current-change', value),
})

const pageSize = computed({
  get: () => props.size,
  set: (value) => emit('size-change', value),
})

const handleSizeChange = (size: number) => {
  emit('size-change', size)
}

const handleCurrentChange = (current: number) => {
  emit('current-change', current)
}
</script>

<style scoped>
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

