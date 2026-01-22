import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getRoutesByRoles, getDefaultRouteByRole } from './permission'
import type { UserRole } from '../api/auth'

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

// 已添加动态路由的角色集合
const addedRoles = new Set<UserRole>()

/**
 * 检查角色的路由是否已添加
 * @param roles 要检查的角色列表
 * @returns 如果所有角色的路由都已添加，返回 true
 */
const hasRolesRoutesAdded = (roles: UserRole[]): boolean => {
  if (roles.length === 0) {
    return false
  }
  // 检查所有角色是否都已添加
  return roles.every((role) => addedRoles.has(role))
}

/**
 * 获取已添加路由的角色列表（用于调试）
 */
export const getAddedRoles = (): UserRole[] => {
  return Array.from(addedRoles)
}

/**
 * 检查指定角色的路由是否已添加
 */
export const hasRoleRoutesAdded = (role: UserRole): boolean => {
  return addedRoles.has(role)
}

/**
 * 添加动态路由
 */
export const addDynamicRoutes = () => {
  const authStore = useAuthStore()
  const roles = authStore.userRoles

  if (!roles || roles.length === 0) {
    return
  }

  if (hasRolesRoutesAdded(roles)) {
    return
  }

  const rolesToAdd = roles.filter((role) => !addedRoles.has(role))

  if (rolesToAdd.length === 0) {
    return
  }

  const roleRoutes = getRoutesByRoles(rolesToAdd)

  roleRoutes.forEach((route) => {
    router.addRoute(route)
  })

  // 记录已添加的角色
  rolesToAdd.forEach((role) => {
    addedRoles.add(role)
  })
}

/**
 * 移除指定角色的动态路由
 * @param roles 要移除的角色列表，如果不传则移除所有角色的路由
 */
export const removeRoleRoutes = (roles?: UserRole[]) => {
  const routes = router.getRoutes()
  const rolesToRemove = roles || Array.from(addedRoles)

  routes.forEach((route) => {
    const routeRoles = route.meta?.roles as UserRole[] | undefined
    if (routeRoles && route.name) {
      // 如果路由属于要移除的角色，则移除该路由
      const shouldRemove = rolesToRemove.some((role) => routeRoles.includes(role))
      if (shouldRemove) {
        router.removeRoute(route.name)
      }
    }
  })

  // 从已添加的角色记录中移除
  if (roles) {
    roles.forEach((role) => addedRoles.delete(role))
  } else {
    addedRoles.clear()
  }
}

/**
 * 重置所有动态路由（清空所有角色的路由）
 */
export const resetDynamicRoutes = () => {
  removeRoleRoutes()
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.title) {
    document.title = `${to.meta.title} - IPC系统`
  }

  if (to.name === 'login') {
    if (authStore.isAuthenticated) {
      const defaultRoute = getDefaultRouteByRole(authStore.userRole ?? null)
      next({ name: defaultRoute })
    } else {
      next()
    }
    return
  }

  if (authStore.isAuthenticated) {
    const userRoles = authStore.userRoles

    // 检查是否需要添加动态路由（根据当前用户的角色）
    if (!hasRolesRoutesAdded(userRoles)) {
      addDynamicRoutes()
      // 重新导航到目标路由，让路由系统重新匹配
      next({path: to.path, replace: true })
      return
    }

    // 检查角色权限（支持多角色，只要有一个角色匹配即可）
    const routeRoles = to.meta.roles as string[] | undefined
    if (routeRoles && routeRoles.length > 0) {
      const hasPermission = userRoles.some((role) => routeRoles.includes(role))

      if (!hasPermission) {
        // 没有权限，跳转到主要角色的首页
        const defaultRoute = getDefaultRouteByRole(authStore.userRole ?? null)
        next({ name: defaultRoute })
        return
      }
    }

    next()
  }
  else
  {
    next({
      name: 'login',
      query: { redirect: to.fullPath },
    })
    return
  }
})

export default router