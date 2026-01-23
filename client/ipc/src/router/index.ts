import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getRoutesByPermissions, getDefaultRouteByRole, getDefaultRouteByPermissions } from './permission'
import type { RouteLocationNormalized } from 'vue-router'

// 基础路由（不需要权限）
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: {
      requiresAuth: false,
      title: '登录',
    },
  },
  // 404 页面 - 捕获所有未匹配的路由，直接渲染组件
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: () => import('../views/NotFoundView.vue'),
    meta: {
      title: '页面不存在',
    },
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
})

// 已添加的路由名称集合（用于去重）
const addedRouteNames = new Set<string>()

/**
 * 检查权限对应的路由是否已添加
 * @param permissions 权限列表
 * @returns 如果所有权限对应的路由都已添加，返回 true
 */
const hasPermissionsRoutesAdded = (permissions: string[]): boolean => {
  if (permissions.length === 0) {
    return false
  }

  // 根据权限获取应该添加的路由列表
  const expectedRoutes = getRoutesByPermissions(permissions)

  if (expectedRoutes.length === 0) {
    // 如果没有应该添加的路由，认为已添加（避免无限循环）
    return true
  }

  // 检查所有应该添加的路由是否都已添加
  // 对于嵌套路由，检查子路由的 name
  return expectedRoutes.every((route) => {
    if (route.children && route.children.length > 0) {
      // 嵌套路由：检查所有子路由是否都已添加
      return route.children.every((child) => {
        return child.name && addedRouteNames.has(child.name as string)
      })
    } else {
      // 普通路由：检查路由本身
      return route.name && addedRouteNames.has(route.name as string)
    }
  })
}

/**
 * 添加动态路由（基于权限）
 */
export const addDynamicRoutes = () => {
  const authStore = useAuthStore()
  const permissions = authStore.userPermissions

  if (!permissions || permissions.length === 0) {
    return
  }

  // 根据权限获取可访问的路由
  const routesToAdd = getRoutesByPermissions(permissions)

  // 添加路由（去重）
  routesToAdd.forEach((route) => {
    if (route.children && route.children.length > 0) {
      // 嵌套路由：添加父路由，并记录所有子路由的 name
      if (!addedRouteNames.has(route.path)) {
        router.addRoute(route)
        // 记录父路由路径，避免重复添加
        addedRouteNames.add(route.path)
      }
      // 记录所有子路由的 name
      route.children.forEach((child) => {
        if (child.name && !addedRouteNames.has(child.name as string)) {
          addedRouteNames.add(child.name as string)
        }
      })
    } else {
      // 普通路由
      if (route.name && !addedRouteNames.has(route.name as string)) {
        router.addRoute(route)
        addedRouteNames.add(route.name as string)
      }
    }
  })
}

/**
 * 重置所有动态路由
 */
export const resetDynamicRoutes = () => {
  if (addedRouteNames.size === 0) {
    return
  }

  // 移除所有动态路由
  const routes = router.getRoutes()
  routes.forEach((route: RouteLocationNormalized | RouteRecordRaw) => {
    // 移除有权限要求的路由（排除基础路由）
    if (route.meta?.permission) {
      if (route.name && addedRouteNames.has(route.name as string)) {
        router.removeRoute(route.name)
        addedRouteNames.delete(route.name as string)
      } else if (route.path && addedRouteNames.has(route.path)) {
        // 处理嵌套路由的父路由（可能没有 name）
        router.removeRoute(route.path)
        addedRouteNames.delete(route.path)
      }
    }
  })
  
  // 清空记录集合
  addedRouteNames.clear()
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - IPC系统`
  }

  // 如果访问登录页
  if (to.name === 'login') {
    if (authStore.isAuthenticated) {
      // 已登录，根据权限或角色跳转到对应首页
      const defaultRoute = getDefaultRouteByPermissions(authStore.userPermissions) 
        || getDefaultRouteByRole(authStore.userRole ?? null)
      next({ name: defaultRoute })
    } else {
      next()
    }
    return
  }

  // 如果已登录
  if (authStore.isAuthenticated) {
    const userPermissions = authStore.userPermissions

    // 检查是否需要添加动态路由（根据当前用户的权限）
    if (!hasPermissionsRoutesAdded(userPermissions)) {
      addDynamicRoutes()
      // 重新导航到目标路由，让路由系统重新匹配
      next({ path: to.path, replace: true })
      return
    }

    // 检查权限（路由需要特定权限才能访问）
    const requiredPermission = to.meta.permission as string | undefined
    if (requiredPermission) {
      const hasPermission = userPermissions.includes(requiredPermission)

      if (!hasPermission) {
        // 没有权限，跳转到默认首页
        const defaultRoute = getDefaultRouteByPermissions(userPermissions) 
          || getDefaultRouteByRole(authStore.userRole ?? null)
        next({ name: defaultRoute })
        return
      }
    }

    next()
  } else {
    // 未登录，检查是否需要认证
    if (to.meta.requiresAuth) {
      next({
        name: 'login',
        query: { redirect: to.fullPath },
      })
      return
    }
    next()
  }
})

export default router