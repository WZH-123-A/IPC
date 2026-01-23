import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, logoutApi, type LoginParams, type UserInfo, type UserRole } from '../api/auth'

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

  // 计算属性：获取用户权限列表
  const userPermissions = computed<string[]>(() => {
    return userInfo.value?.permissions || []
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

  // 初始化时检查登录状态
  checkAuth()

  return {
    token,
    userInfo,
    isAuthenticated,
    userRoles,
    userRole,
    userPermissions,
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
  }
})
