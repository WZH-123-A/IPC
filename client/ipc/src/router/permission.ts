import type { RouteRecordRaw } from 'vue-router'
import type { UserRole } from '../api/auth'

/**
 * 路由权限配置
 * 定义每个路由需要的权限编码（由后端权限系统控制）
 */
import AdminLayout from '../layouts/admin/AdminLayout.vue'

/**
 * 路由权限配置
 * 定义每个路由需要的权限编码（由后端权限系统控制）
 */
export const permissionRoutes: RouteRecordRaw[] = [
  // 管理员相关路由（使用布局组件）
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/home',
    meta: {
      requiresAuth: true,
      permission: 'admin:home',
    },
    children: [
      {
        path: 'home',
        name: 'admin-home',
        component: () => import('../views/admin/AdminHomeView.vue'),
        meta: {
          title: '管理后台',
          requiresAuth: true,
          permission: 'admin:home',
        },
      },
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('../views/admin/UserManageView.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          permission: 'admin:users',
        },
      },
      {
        path: 'roles',
        name: 'admin-roles',
        component: () => import('../views/admin/RoleManageView.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true,
          permission: 'admin:roles',
        },
      },
      {
        path: 'permissions',
        name: 'admin-permissions',
        component: () => import('../views/admin/PermissionManageView.vue'),
        meta: {
          title: '权限管理',
          requiresAuth: true,
          permission: 'admin:permissions',
        },
      },
    ],
  },
  // 患者相关路由
  {
    path: '/patient/home',
    name: 'patient-home',
    component: () => import('../views/patient/PatientHomeView.vue'),
    meta: {
      title: '患者首页',
      requiresAuth: true,
      permission: 'patient:home',
    },
  },
  {
    path: '/patient/consultation',
    name: 'patient-consultation',
    component: () => import('../views/patient/ConsultationView.vue'),
    meta: {
      title: '在线问诊',
      requiresAuth: true,
      permission: 'patient:consultation',
    },
  },
  {
    path: '/patient/diagnosis',
    name: 'patient-diagnosis',
    component: () => import('../views/patient/DiagnosisView.vue'),
    meta: {
      title: '皮肤诊断',
      requiresAuth: true,
      permission: 'patient:diagnosis',
    },
  },
  // 医生相关路由
  {
    path: '/doctor/home',
    name: 'doctor-home',
    component: () => import('../views/doctor/DoctorHomeView.vue'),
    meta: {
      title: '医生首页',
      requiresAuth: true,
      permission: 'doctor:home',
    },
  },
  {
    path: '/doctor/consultation',
    name: 'doctor-consultation',
    component: () => import('../views/doctor/ConsultationListView.vue'),
    meta: {
      title: '问诊管理',
      requiresAuth: true,
      permission: 'doctor:consultation',
    },
  },
  {
    path: '/doctor/patients',
    name: 'doctor-patients',
    component: () => import('../views/doctor/PatientListView.vue'),
    meta: {
      title: '患者管理',
      requiresAuth: true,
      permission: 'doctor:patients',
    },
  },
]

/**
 * 根据权限列表获取可访问的路由
 * @param permissions 用户拥有的权限编码列表
 * @returns 可访问的路由列表
 */
export const getRoutesByPermissions = (permissions: string[]): RouteRecordRaw[] => {
  if (!permissions || permissions.length === 0) {
    return []
  }

  const permissionSet = new Set(permissions)
  const routeNames = new Set<string>()

  // 过滤出用户有权限访问的路由
  const filteredRoutes: RouteRecordRaw[] = []
  
  permissionRoutes.forEach((route) => {
    // 处理嵌套路由（如管理员路由）
    if (route.children && route.children.length > 0) {
      const filteredChildren = route.children.filter((child) => {
        const permission = child.meta?.permission as string | undefined
        if (!permission) return false
        
        const hasPermission = permissionSet.has(permission)
        if (hasPermission && child.name && !routeNames.has(child.name as string)) {
          routeNames.add(child.name as string)
          return true
        }
        return false
      })
      
      if (filteredChildren.length > 0) {
        filteredRoutes.push({
          ...route,
          children: filteredChildren,
        })
      }
    } else {
      // 处理普通路由
      const permission = route.meta?.permission as string | undefined
      if (permission) {
        const hasPermission = permissionSet.has(permission)
        if (hasPermission && route.name && !routeNames.has(route.name as string)) {
          routeNames.add(route.name as string)
          filteredRoutes.push(route)
        }
      }
    }
  })

  return filteredRoutes
}

/**
 * 根据角色获取默认首页路由名称（用于登录后跳转，兼容旧逻辑）
 */
export const getDefaultRouteByRole = (role: UserRole | null): string => {
  if (!role) {
    return 'login'
  }
  const roleDefaultRoutes: Record<UserRole, string> = {
    patient: 'patient-home',
    doctor: 'doctor-home',
    admin: 'admin-home',
  }
  return roleDefaultRoutes[role]
}

/**
 * 根据权限获取默认首页路由名称
 * @param permissions 用户权限列表
 * @returns 默认首页路由名称
 */
export const getDefaultRouteByPermissions = (permissions: string[]): string | null => {
  // 优先级：admin > doctor > patient
  if (permissions.includes('admin:home')) {
    return 'admin-home'
  }
  if (permissions.includes('doctor:home')) {
    return 'doctor-home'
  }
  if (permissions.includes('patient:home')) {
    return 'patient-home'
  }
  return null
}