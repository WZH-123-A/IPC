<template>
  <aside class="admin-sidebar">
    <el-menu
      :default-active="activeMenu"
      router
      class="sidebar-menu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/admin/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <!-- 系统管理 -->
      <el-sub-menu index="system">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/roles">
          <el-icon><UserFilled /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/permissions">
          <el-icon><Key /></el-icon>
          <span>权限管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/files">
          <el-icon><Document /></el-icon>
          <span>文件管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/access-logs">
          <el-icon><List /></el-icon>
          <span>访问日志</span>
        </el-menu-item>
        <el-menu-item index="/admin/operation-logs">
          <el-icon><DocumentCopy /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 用户信息管理 -->
      <el-sub-menu index="user-info">
        <template #title>
          <el-icon><Avatar /></el-icon>
          <span>用户信息</span>
        </template>
        <el-menu-item index="/admin/patient-info">
          <el-icon><User /></el-icon>
          <span>患者信息</span>
        </el-menu-item>
        <el-menu-item index="/admin/doctor-info">
          <el-icon><Avatar /></el-icon>
          <span>医生信息</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 问诊管理 -->
      <el-sub-menu index="consultation">
        <template #title>
          <el-icon><ChatLineRound /></el-icon>
          <span>问诊管理</span>
        </template>
        <el-menu-item index="/admin/consultation-sessions">
          <el-icon><Message /></el-icon>
          <span>问诊会话</span>
        </el-menu-item>
        <el-menu-item index="/admin/consultation-messages">
          <el-icon><ChatDotRound /></el-icon>
          <span>问诊消息</span>
        </el-menu-item>
        <el-menu-item index="/admin/consultation-evaluations">
          <el-icon><Star /></el-icon>
          <span>问诊评价</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 诊断管理 -->
      <el-sub-menu index="diagnosis">
        <template #title>
          <el-icon><Search /></el-icon>
          <span>诊断管理</span>
        </template>
        <el-menu-item index="/admin/skin-diagnosis-records">
          <el-icon><Picture /></el-icon>
          <span>皮肤诊断记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/diagnosis-results">
          <el-icon><DocumentChecked /></el-icon>
          <span>诊断结果</span>
        </el-menu-item>
        <el-menu-item index="/admin/disease-types">
          <el-icon><CollectionTag /></el-icon>
          <span>疾病类型</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 知识库管理 -->
      <el-sub-menu index="knowledge">
        <template #title>
          <el-icon><Reading /></el-icon>
          <span>知识库管理</span>
        </template>
        <el-menu-item index="/admin/knowledge-categories">
          <el-icon><Folder /></el-icon>
          <span>知识库分类</span>
        </el-menu-item>
        <el-menu-item index="/admin/knowledge-contents">
          <el-icon><Document /></el-icon>
          <span>知识库内容</span>
        </el-menu-item>
        <el-menu-item index="/admin/knowledge-tags">
          <el-icon><PriceTag /></el-icon>
          <span>知识库标签</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 统计分析 -->
      <el-sub-menu index="statistics">
        <template #title>
          <el-icon><DataAnalysis /></el-icon>
          <span>统计分析</span>
        </template>
        <el-menu-item index="/admin/statistics-daily">
          <el-icon><TrendCharts /></el-icon>
          <span>每日统计</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeFilled,
  User,
  UserFilled,
  Key,
  Setting,
  Document,
  List,
  DocumentCopy,
  Avatar,
  ChatLineRound,
  Message,
  ChatDotRound,
  Star,
  Search,
  Picture,
  DocumentChecked,
  CollectionTag,
  Reading,
  Folder,
  PriceTag,
  DataAnalysis,
  TrendCharts,
} from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-sidebar {
  width: 200px;
  background-color: #304156;
  overflow-y: auto;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}
</style>

