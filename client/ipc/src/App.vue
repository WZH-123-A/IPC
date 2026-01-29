<script setup lang="ts">
import { onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from './stores/auth'
import { websocketClient } from './utils/websocket'

let unsubscribePermissionRefresh: (() => void) | null = null

function setupPermissionRefreshSubscription(authStore: ReturnType<typeof useAuthStore>) {
  if (unsubscribePermissionRefresh) return
  unsubscribePermissionRefresh = websocketClient.subscribeToPermissionRefresh((payload) => {
    const myId = authStore.userInfo?.id ?? authStore.userInfo?.userId
    if (payload.userId != null && myId != null && payload.userId === myId) {
      authStore.refreshPermissions().catch((e) => console.error('收到权限刷新通知后拉取失败:', e))
    }
  })
}

function ensureConnectAndSubscribe(authStore: ReturnType<typeof useAuthStore>) {
  if (!authStore.isAuthenticated || !authStore.token) return
  if (websocketClient.isConnected()) {
    setupPermissionRefreshSubscription(authStore)
  } else if (authStore.token) {
    websocketClient
      .connect(authStore.token)
      .then(() => setupPermissionRefreshSubscription(authStore))
      .catch(console.error)
  }
}

onMounted(() => {
  const authStore = useAuthStore()
  ensureConnectAndSubscribe(authStore)
})

// 登录成功后也会触发：此时 isAuthenticated 从 false 变为 true，需连接 WS 并订阅权限刷新
watch(
  () => useAuthStore().isAuthenticated,
  (isAuth) => {
    if (!isAuth) return
    const authStore = useAuthStore()
    ensureConnectAndSubscribe(authStore)
  }
)

onUnmounted(() => {
  if (unsubscribePermissionRefresh) {
    unsubscribePermissionRefresh()
    unsubscribePermissionRefresh = null
  }
})
</script>

<template>
  <RouterView />
</template>
