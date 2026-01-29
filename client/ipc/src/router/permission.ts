import type { RouteRecordRaw } from 'vue-router'
import type { UserRole } from '../api/auth'

/**
 * 路由权限配置
 * 定义每个路由需要的权限编码（由后端权限系统控制）
 */
import AdminLayout from '../layouts/admin/AdminLayout.vue'
import PatientLayout from '../layouts/patient/PatientLayout.vue'
import DoctorLayout from '../layouts/doctor/DoctorLayout.vue'

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
        component: () => import('../views/admin/home/HomeManager.vue'),
        meta: {
          title: '管理后台',
          requiresAuth: true,
          permission: 'admin:home',
        },
      },
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('../views/admin/system/UsersManager.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          permission: 'admin:users',
        },
      },
      {
        path: 'roles',
        name: 'admin-roles',
        component: () => import('../views/admin/system/RolesManager.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true,
          permission: 'admin:roles',
        },
      },
      {
        path: 'permissions',
        name: 'admin-permissions',
        component: () => import('../views/admin/system/PermissionsManager.vue'),
        meta: {
          title: '权限管理',
          requiresAuth: true,
          permission: 'admin:permissions',
        },
      },
      // 系统管理
      {
        path: 'files',
        name: 'admin-files',
        component: () => import('../views/admin/system/FilesManager.vue'),
        meta: {
          title: '文件管理',
          requiresAuth: true,
          permission: 'admin:files',
        },
      },
      {
        path: 'access-logs',
        name: 'admin-access-logs',
        component: () => import('../views/admin/system/AccessLogsManager.vue'),
        meta: {
          title: '访问日志',
          requiresAuth: true,
          permission: 'admin:access-logs',
        },
      },
      {
        path: 'operation-logs',
        name: 'admin-operation-logs',
        component: () => import('../views/admin/system/OperationLogsManager.vue'),
        meta: {
          title: '操作日志',
          requiresAuth: true,
          permission: 'admin:operation-logs',
        },
      },
      // 用户信息管理
      {
        path: 'patient-info',
        name: 'admin-patient-info',
        component: () => import('../views/admin/user-info/PatientInfoManager.vue'),
        meta: {
          title: '患者信息',
          requiresAuth: true,
          permission: 'admin:patient-info',
        },
      },
      {
        path: 'doctor-info',
        name: 'admin-doctor-info',
        component: () => import('../views/admin/user-info/DoctorInfoManager.vue'),
        meta: {
          title: '医生信息',
          requiresAuth: true,
          permission: 'admin:doctor-info',
        },
      },
      // 问诊管理
      {
        path: 'consultation-sessions',
        name: 'admin-consultation-sessions',
        component: () => import('../views/admin/consultation/SessionsManager.vue'),
        meta: {
          title: '问诊会话',
          requiresAuth: true,
          permission: 'admin:consultation-sessions',
        },
      },
      {
        path: 'consultation-messages',
        name: 'admin-consultation-messages',
        component: () => import('../views/admin/consultation/MessagesManager.vue'),
        meta: {
          title: '问诊消息',
          requiresAuth: true,
          permission: 'admin:consultation-messages',
        },
      },
      {
        path: 'consultation-evaluations',
        name: 'admin-consultation-evaluations',
        component: () => import('../views/admin/consultation/EvaluationsManager.vue'),
        meta: {
          title: '问诊评价',
          requiresAuth: true,
          permission: 'admin:consultation-evaluations',
        },
      },
      // 诊断管理
      {
        path: 'skin-diagnosis-records',
        name: 'admin-skin-diagnosis-records',
        component: () => import('../views/admin/diagnosis/SkinDiagnosisRecordsManager.vue'),
        meta: {
          title: '皮肤诊断记录',
          requiresAuth: true,
          permission: 'admin:skin-diagnosis-records',
        },
      },
      {
        path: 'diagnosis-results',
        name: 'admin-diagnosis-results',
        component: () => import('../views/admin/diagnosis/ResultsManager.vue'),
        meta: {
          title: '诊断结果',
          requiresAuth: true,
          permission: 'admin:diagnosis-results',
        },
      },
      {
        path: 'disease-types',
        name: 'admin-disease-types',
        component: () => import('../views/admin/diagnosis/DiseaseTypesManager.vue'),
        meta: {
          title: '疾病类型',
          requiresAuth: true,
          permission: 'admin:disease-types',
        },
      },
      // 知识库管理
      {
        path: 'knowledge-categories',
        name: 'admin-knowledge-categories',
        component: () => import('../views/admin/knowledge/CategoriesManager.vue'),
        meta: {
          title: '知识库分类',
          requiresAuth: true,
          permission: 'admin:knowledge-categories',
        },
      },
      {
        path: 'knowledge-contents',
        name: 'admin-knowledge-contents',
        component: () => import('../views/admin/knowledge/ContentsManager.vue'),
        meta: {
          title: '知识库内容',
          requiresAuth: true,
          permission: 'admin:knowledge-contents',
        },
      },
      {
        path: 'knowledge-tags',
        name: 'admin-knowledge-tags',
        component: () => import('../views/admin/knowledge/TagsManager.vue'),
        meta: {
          title: '知识库标签',
          requiresAuth: true,
          permission: 'admin:knowledge-tags',
        },
      },
      // 统计分析
      {
        path: 'statistics-daily',
        name: 'admin-statistics-daily',
        component: () => import('../views/admin/statistics/DailyManager.vue'),
        meta: {
          title: '每日统计',
          requiresAuth: true,
          permission: 'admin:statistics-daily',
        },
      },
    ],
  },
  // 患者相关路由（使用布局组件）
  {
    path: '/patient',
    component: PatientLayout,
    redirect: '/patient/home',
    meta: {
      requiresAuth: true,
      permission: 'patient:home',
    },
    children: [
      {
        path: 'home',
        name: 'patient-home',
        component: () => import('../views/patient/PatientHomeView.vue'),
        meta: {
          title: '患者首页',
          requiresAuth: true,
          permission: 'patient:home',
        },
      },
      {
        path: 'consultation',
        name: 'patient-consultation',
        component: () => import('../views/patient/ConsultationView.vue'),
        meta: {
          title: '在线问诊',
          requiresAuth: true,
          permission: 'patient:consultation',
        },
      },
      {
        path: 'diagnosis',
        name: 'patient-diagnosis',
        component: () => import('../views/patient/DiagnosisView.vue'),
        meta: {
          title: '皮肤诊断',
          requiresAuth: true,
          permission: 'patient:diagnosis',
        },
      },
      {
        path: 'knowledge',
        name: 'patient-knowledge',
        component: () => import('../views/patient/KnowledgeView.vue'),
        meta: {
          title: '病知识库',
          requiresAuth: true,
          permission: 'patient:knowledge',
        },
      },
    ],
  },
  // 医生相关路由（使用布局组件）
  {
    path: '/doctor',
    component: DoctorLayout,
    redirect: '/doctor/home',
    meta: {
      requiresAuth: true,
      permission: 'doctor:home',
    },
    children: [
      {
        path: 'home',
        name: 'doctor-home',
        component: () => import('../views/doctor/DoctorHomeView.vue'),
        meta: {
          title: '工作台',
          requiresAuth: true,
          permission: 'doctor:home',
        },
      },
      {
        path: 'consultation/chat',
        name: 'doctor-consultation-chat',
        component: () => import('../views/doctor/ConsultationChatView.vue'),
        meta: {
          title: '在线问诊',
          requiresAuth: true,
          permission: 'doctor:consultation',
        },
      },
      {
        path: 'consultation',
        name: 'doctor-consultation',
        component: () => import('../views/doctor/ConsultationListView.vue'),
        meta: {
          title: '问诊管理',
          requiresAuth: true,
          permission: 'doctor:consultation',
        },
      },
      {
        path: 'patients',
        name: 'doctor-patients',
        component: () => import('../views/doctor/PatientListView.vue'),
        meta: {
          title: '患者管理',
          requiresAuth: true,
          permission: 'doctor:patients',
        },
      },
    ],
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