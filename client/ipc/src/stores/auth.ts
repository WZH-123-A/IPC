import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, logoutApi, type LoginParams, type UserInfo } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token') || null)
  const userInfo = ref<UserInfo | null>(null)
  const isAuthenticated = ref<boolean>(!!token.value)

  // 登录
  const login = async (params: LoginParams) => {
    try {
      // 调用登录 API
      const response = await loginApi(params)
      
      // 更新状态
      token.value = response.token
      userInfo.value = response.userInfo
      isAuthenticated.value = true
      
      // 保存 token 和用户信息
      if (params.rememberMe) {
        localStorage.setItem('token', response.token)
        localStorage.setItem('userInfo', JSON.stringify(response.userInfo))
      } else {
        sessionStorage.setItem('token', response.token)
        sessionStorage.setItem('userInfo', JSON.stringify(response.userInfo))
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
    login,
    logout,
    checkAuth,
  }
})
