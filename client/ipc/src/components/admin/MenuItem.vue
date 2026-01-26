<template>
  <!-- 有子菜单的情况 -->
  <el-sub-menu v-if="menuItem.children && menuItem.children.length > 0" :index="menuItem.index">
    <template #title>
      <el-icon v-if="icon">
        <component :is="icon" />
      </el-icon>
      <span>{{ menuItem.title }}</span>
    </template>
    <!-- 递归渲染子菜单 -->
    <MenuItem
      v-for="child in menuItem.children"
      :key="child.index"
      :menu-item="child"
      :icon-map="iconMap"
      :permission-to-icon-map="permissionToIconMap"
    />
  </el-sub-menu>
  <!-- 单个菜单项 -->
  <el-menu-item v-else :index="menuItem.index">
    <el-icon v-if="icon">
      <component :is="icon" />
    </el-icon>
    <span>{{ menuItem.title }}</span>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuItem } from '../../utils/permission'

interface Props {
  menuItem: MenuItem
  iconMap: Record<string, unknown>
  permissionToIconMap: Record<string, string>
}

const props = defineProps<Props>()

// 根据权限编码获取图标
const icon = computed(() => {
  const iconName = props.permissionToIconMap[props.menuItem.permissionCode]
  return iconName ? props.iconMap[iconName] : null
})
</script>

