import type { Directive, DirectiveBinding } from 'vue'
import type { UserPermissionTreeNode } from '../api/userPermissions'
import { extractPermissionCodesByType } from '../api/userPermissions'

function applyPermission(el: HTMLElement, permissionCode: string): void {
  if (!permissionCode) return
  const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
  if (!userInfoStr) {
    el.style.display = 'none'
    return
  }
  try {
    const userInfo = JSON.parse(userInfoStr)
    const permissionTree: UserPermissionTreeNode[] = userInfo.permissions || []
    const permissions = extractPermissionCodesByType(permissionTree, 2) // 只提取按钮权限
    if (permissions.includes(permissionCode)) {
      el.style.display = ''
    } else {
      el.style.display = 'none'
    }
  } catch (error) {
    console.error('Failed to parse user info:', error)
    el.style.display = 'none'
  }
}

/**
 * 权限指令
 * 用法: v-permission="'permission:code'"
 * 如果用户没有该权限，元素将被隐藏。
 * 监听 permission-refresh 事件，刷新权限后按钮显隐会自动更新。
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
    const permissionCode = binding.value
    if (!permissionCode) return
    applyPermission(el, permissionCode)

    const handler = () => applyPermission(el, permissionCode)
    window.addEventListener('permission-refresh', handler)
    ;(el as HTMLElement & { _permissionRefreshHandler?: () => void })._permissionRefreshHandler = handler
  },
  updated(el: HTMLElement, binding: DirectiveBinding<string>) {
    applyPermission(el, binding.value)
  },
  unmounted(el: HTMLElement) {
    const handler = (el as HTMLElement & { _permissionRefreshHandler?: () => void })._permissionRefreshHandler
    if (handler) {
      window.removeEventListener('permission-refresh', handler)
      delete (el as HTMLElement & { _permissionRefreshHandler?: () => void })._permissionRefreshHandler
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
    const permissionTree: UserPermissionTreeNode[] = userInfo.permissions || []
    const permissions = extractPermissionCodesByType(permissionTree, 2) // 只提取按钮权限
    return permissions.includes(permissionCode)
  } catch (error) {
    console.error('Failed to parse user info:', error)
    return false
  }
}

