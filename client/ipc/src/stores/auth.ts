import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, logoutApi, refreshPermissionsApi, type LoginParams, type UserInfo, type UserRole } from '../api/auth'
import { extractPermissionCodes, extractPermissionCodesByType } from '../api/userPermissions'
import { websocketClient } from '../utils/websocket'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token') || null)
  const userInfo = ref<UserInfo | null>(null)
  const isAuthenticated = ref<boolean>(!!token.value)

  // 计算属性：获取用户角色列表
  const userRoles = computed<UserRole[]>(() => {
    return userInfo.value?.roles || []
  })

  // 计算属性：获取主要角色（第一个角色，用于路由跳转）
  const userRole = computed(() => {
    return userRoles.value.length > 0 ? userRoles.value[0] : null
  })

  // 计算属性：是否为患者
  const isPatient = computed(() => userRoles.value.includes('patient'))

  // 计算属性：是否为医生
  const isDoctor = computed(() => userRoles.value.includes('doctor'))

  // 计算属性：是否为管理员
  const isAdmin = computed(() => userRoles.value.includes('admin'))

  // 计算属性：获取用户权限编码列表（从权限树中提取）
  const userPermissions = computed<string[]>(() => {
    if (!userInfo.value?.permissions) {
      return []
    }
    return extractPermissionCodes(userInfo.value.permissions)
  })

  // 计算属性：获取菜单权限编码列表（permissionType === 1）
  const userMenuPermissions = computed<string[]>(() => {
    if (!userInfo.value?.permissions) {
      return []
    }
    return extractPermissionCodesByType(userInfo.value.permissions, 1)
  })

  // 计算属性：获取按钮权限编码列表（permissionType === 2）
  const userButtonPermissions = computed<string[]>(() => {
    if (!userInfo.value?.permissions) {
      return []
    }
    return extractPermissionCodesByType(userInfo.value.permissions, 2)
  })

  // 计算属性：获取接口权限编码列表（permissionType === 3）
  const userApiPermissions = computed<string[]>(() => {
    if (!userInfo.value?.permissions) {
      return []
    }
    return extractPermissionCodesByType(userInfo.value.permissions, 3)
  })

  // 计算属性：获取路由权限编码列表（permissionType === 4）
  const userRoutePermissions = computed<string[]>(() => {
    if (!userInfo.value?.permissions) {
      return []
    }
    return extractPermissionCodesByType(userInfo.value.permissions, 4)
  })

  // 计算属性：是否拥有指定角色
  const hasRole = (role: UserRole) => {
    return userRoles.value.includes(role)
  }

  // 计算属性：是否拥有指定权限
  const hasPermission = (permission: string) => {
    return userPermissions.value.includes(permission)
  }

  // 计算属性：是否拥有任意一个权限
  const hasAnyPermission = (permissions: string[]) => {
    return permissions.some((permission) => userPermissions.value.includes(permission))
  }

  // 计算属性：是否拥有所有权限
  const hasAllPermissions = (permissions: string[]) => {
    return permissions.every((permission) => userPermissions.value.includes(permission))
  }

  // 登录
  const login = async (params: LoginParams) => {
    try {
      // 调用登录 API
      const response = await loginApi(params)
      
      // 将后端返回的数据转换为前端 UserInfo 格式
      const userInfoData: UserInfo = {
        id: response.userId,
        userId: response.userId,
        username: response.username,
        realName: response.realName,
        roles: response.roles || [],
        permissions: response.permissions || [],
      }
      
      // 更新状态
      token.value = response.token
      userInfo.value = userInfoData
      isAuthenticated.value = true
      
      // 保存 token 和用户信息
      if (params.rememberMe) {
        localStorage.setItem('token', response.token)
        localStorage.setItem('userInfo', JSON.stringify(userInfoData))
      } else {
        sessionStorage.setItem('token', response.token)
        sessionStorage.setItem('userInfo', JSON.stringify(userInfoData))
      }
      
      // 登录成功后连接WebSocket
      try {
        await websocketClient.connect(response.token)
        console.log('WebSocket连接成功')
      } catch (error) {
        console.error('WebSocket连接失败:', error)
        // WebSocket连接失败不影响登录流程
      }
    } catch (error: unknown) {
      // 清除可能的部分状态
      token.value = null
      userInfo.value = null
      isAuthenticated.value = false
      throw error
    }
  }

  // 登出
  const logout = async () => {
    try {
      // 调用登出 API
      await logoutApi()
    } catch (error: unknown) {
      // 即使 API 调用失败，也清除本地状态
      console.error('登出 API 调用失败:', error)
    } finally {
      // 断开WebSocket连接
      try {
        websocketClient.disconnect()
      } catch (error) {
        console.error('断开WebSocket连接失败:', error)
      }
      
      // 清除状态
      token.value = null
      userInfo.value = null
      isAuthenticated.value = false
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userInfo')
      
      // 重置动态路由
      const { resetDynamicRoutes } = await import('../router')
      resetDynamicRoutes()
    }
  }

  // 检查登录状态
  const checkAuth = () => {
    const storedToken = localStorage.getItem('token') || sessionStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
    
    if (storedToken) {
      token.value = storedToken
      isAuthenticated.value = true
    }
    
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  // 刷新权限
  const refreshPermissions = async () => {
    try {
      if (!userInfo.value) {
        console.warn('用户未登录，无法刷新权限')
        return
      }

      // 调用 API 获取最新权限
      const newPermissions = await refreshPermissionsApi()

      // 更新 userInfo 中的权限
      userInfo.value = {
        ...userInfo.value,
        permissions: newPermissions,
      }

      // 更新本地存储
      const userInfoStr = JSON.stringify(userInfo.value)
      if (localStorage.getItem('token')) {
        localStorage.setItem('userInfo', userInfoStr)
      } else if (sessionStorage.getItem('token')) {
        sessionStorage.setItem('userInfo', userInfoStr)
      }

      // 通知各布局/侧边栏重新根据权限树渲染菜单
      window.dispatchEvent(new CustomEvent('permission-refresh'))
    } catch (error: unknown) {
      console.error('刷新权限失败:', error)
      throw error
    }
  }

  // 初始化时检查登录状态
  checkAuth()

  return {
    token,
    userInfo,
    isAuthenticated,
    userRoles,
    userRole,
    userPermissions,
    userMenuPermissions,
    userButtonPermissions,
    userApiPermissions,
    userRoutePermissions,
    isPatient,
    isDoctor,
    isAdmin,
    hasRole,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    login,
    logout,
    checkAuth,
    refreshPermissions,
  }
})
