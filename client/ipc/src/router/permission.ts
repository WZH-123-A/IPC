import type { RouteRecordRaw } from 'vue-router'
import type { UserRole } from '../api/auth'

/**
 * 角色权限配置
 * 定义每个角色可以访问的路由
 */
export const roleRoutes: Record<UserRole, RouteRecordRaw[]> = {
  patient: [
    {
      path: '/patient/home',
      name: 'patient-home',
      component: () => import('../views/patient/PatientHomeView.vue'),
      meta: {
        title: '患者首页',
        requiresAuth: true,
        roles: ['patient'],
      },
    },
    {
      path: '/patient/consultation',
      name: 'patient-consultation',
      component: () => import('../views/patient/ConsultationView.vue'),
      meta: {
        title: '在线问诊',
        requiresAuth: true,
        roles: ['patient'],
      },
    },
    {
      path: '/patient/diagnosis',
      name: 'patient-diagnosis',
      component: () => import('../views/patient/DiagnosisView.vue'),
      meta: {
        title: '皮肤诊断',
        requiresAuth: true,
        roles: ['patient'],
      },
    },
  ],
  doctor: [
    {
      path: '/doctor/home',
      name: 'doctor-home',
      component: () => import('../views/doctor/DoctorHomeView.vue'),
      meta: {
        title: '医生首页',
        requiresAuth: true,
        roles: ['doctor'],
      },
    },
    {
      path: '/doctor/consultation',
      name: 'doctor-consultation',
      component: () => import('../views/doctor/ConsultationListView.vue'),
      meta: {
        title: '问诊管理',
        requiresAuth: true,
        roles: ['doctor'],
      },
    },
    {
      path: '/doctor/patients',
      name: 'doctor-patients',
      component: () => import('../views/doctor/PatientListView.vue'),
      meta: {
        title: '患者管理',
        requiresAuth: true,
        roles: ['doctor'],
      },
    },
  ],
  admin: [
    {
      path: '/admin/home',
      name: 'admin-home',
      component: () => import('../views/admin/AdminHomeView.vue'),
      meta: {
        title: '管理后台',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/admin/UserManageView.vue'),
      meta: {
        title: '用户管理',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
    {
      path: '/admin/roles',
      name: 'admin-roles',
      component: () => import('../views/admin/RoleManageView.vue'),
      meta: {
        title: '角色管理',
        requiresAuth: true,
        roles: ['admin'],
      },
    },
  ],
}

/**
 * 根据角色获取默认首页路由名称（取第一个角色）
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
 * 根据角色列表获取路由列表（合并所有角色的路由，去重）
 */
export const getRoutesByRoles = (roles: UserRole[]): RouteRecordRaw[] => {
  const allRoutes: RouteRecordRaw[] = []
  const routeNames = new Set<string>()

  roles.forEach((role) => {
    const routes = roleRoutes[role] || []
    routes.forEach((route) => {
      if (route.name && !routeNames.has(route.name as string)) {
        routeNames.add(route.name as string)
        allRoutes.push(route)
      }
    })
  })

  return allRoutes
}

/**
 * 根据单个角色获取路由列表（兼容旧代码）
 */
export const getRoutesByRole = (role: UserRole): RouteRecordRaw[] => {
  return getRoutesByRoles([role])
}
