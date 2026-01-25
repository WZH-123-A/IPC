import type { Directive, DirectiveBinding } from 'vue'

/**
 * 权限指令
 * 用法: v-permission="'permission:code'"
 * 如果用户没有该权限，元素将被隐藏
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
    const permissionCode = binding.value
    if (!permissionCode) {
      return
    }

    // 从 localStorage 或 store 获取用户权限列表
    const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
    if (!userInfoStr) {
      el.style.display = 'none'
      return
    }

    try {
      const userInfo = JSON.parse(userInfoStr)
      const permissions: string[] = userInfo.permissions || []

      if (!permissions.includes(permissionCode)) {
        el.style.display = 'none'
      }
    } catch (error) {
      console.error('Failed to parse user info:', error)
      el.style.display = 'none'
    }
  },
  updated(el: HTMLElement, binding: DirectiveBinding<string>) {
    const permissionCode = binding.value
    if (!permissionCode) {
      return
    }

    const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
    if (!userInfoStr) {
      el.style.display = 'none'
      return
    }

    try {
      const userInfo = JSON.parse(userInfoStr)
      const permissions: string[] = userInfo.permissions || []

      if (permissions.includes(permissionCode)) {
        el.style.display = ''
      } else {
        el.style.display = 'none'
      }
    } catch (error) {
      console.error('Failed to parse user info:', error)
      el.style.display = 'none'
    }
  },
}

/**
 * 检查用户是否有指定权限
 */
export function hasPermission(permissionCode: string): boolean {
  const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
  if (!userInfoStr) {
    return false
  }

  try {
    const userInfo = JSON.parse(userInfoStr)
    const permissions: string[] = userInfo.permissions || []
    return permissions.includes(permissionCode)
  } catch (error) {
    console.error('Failed to parse user info:', error)
    return false
  }
}

