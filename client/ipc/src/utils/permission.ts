import type { UserPermissionTreeNode } from '../api/userPermissions'

/**
 * 菜单项接口
 */
export interface MenuItem {
  index: string
  title: string
  permissionCode: string
  children?: MenuItem[]
}

/**
 * 权限编码到路由路径的映射
 */
export const permissionToRouteMap: Record<string, string> = {
  'admin:home': '/admin/home',
  'system:user:menu': '/admin/users',
  'system:role:menu': '/admin/roles',
  'system:permission:menu': '/admin/permissions',
  'system:file:menu': '/admin/files',
  'system:access-log:menu': '/admin/access-logs',
  'system:operation-log:menu': '/admin/operation-logs',
  'patient:list:menu': '/admin/patient-list',
  'doctor:list:menu': '/admin/doctor-list',
  'consultation:list:menu': '/admin/consultation-list',
  'consultation:session:menu': '/admin/consultation-sessions',
  'consultation:message:menu': '/admin/consultation-messages',
  'consultation:evaluation:menu': '/admin/consultation-evaluations',
  'diagnosis:list:menu': '/admin/diagnosis-list',
  'diagnosis:record:menu': '/admin/skin-diagnosis-records',
  'diagnosis:result:menu': '/admin/diagnosis-results',
  'diagnosis:disease-type:menu': '/admin/disease-types',
  'knowledge:list:menu': '/admin/knowledge-list',
  'knowledge:category:menu': '/admin/knowledge-categories',
  'knowledge:content:menu': '/admin/knowledge-contents',
  'knowledge:tag:menu': '/admin/knowledge-tags',
  'user-info:patient:menu': '/admin/patient-info',
  'user-info:doctor:menu': '/admin/doctor-info',
  'statistics:daily:menu': '/admin/statistics-daily',
}

/**
 * 权限编码到图标名称的映射（简化版，实际可以使用更复杂的映射）
 */
export const permissionToIconMap: Record<string, string> = {
  'admin:home': 'HomeFilled',
  'system:user:menu': 'User',
  'system:role:menu': 'UserFilled',
  'system:permission:menu': 'Key',
  'system:file:menu': 'Document',
  'system:access-log:menu': 'List',
  'system:operation-log:menu': 'DocumentCopy',
  'user-info:patient:menu': 'User',
  'user-info:doctor:menu': 'Avatar',
  'consultation:session:menu': 'Message',
  'consultation:message:menu': 'ChatDotRound',
  'consultation:evaluation:menu': 'Star',
  'diagnosis:record:menu': 'Picture',
  'diagnosis:result:menu': 'DocumentChecked',
  'diagnosis:disease-type:menu': 'CollectionTag',
  'knowledge:category:menu': 'Folder',
  'knowledge:content:menu': 'Document',
  'knowledge:tag:menu': 'PriceTag',
  'statistics:daily:menu': 'TrendCharts',
  // 父级菜单图标
  'system:menu': 'Setting',
  'user-info:menu': 'Avatar',
  'consultation:menu': 'ChatLineRound',
  'diagnosis:menu': 'Search',
  'knowledge:menu': 'Reading',
  'statistics:menu': 'DataAnalysis',
}

/** 患者端菜单：权限编码 -> 路由路径 */
export const patientPermissionToRouteMap: Record<string, string> = {
  'patient:home:menu': '/patient/home',
  'patient:consultation:menu': '/patient/consultation',
  'patient:diagnosis:menu': '/patient/diagnosis',
  'patient:knowledge:menu': '/patient/knowledge',
}

/** 患者端菜单：权限编码 -> 图标名 */
export const patientPermissionToIconMap: Record<string, string> = {
  'patient:home:menu': 'HomeFilled',
  'patient:consultation:menu': 'ChatDotRound',
  'patient:diagnosis:menu': 'Search',
  'patient:knowledge:menu': 'Reading',
}

/** 医生端菜单：权限编码 -> 路由路径 */
export const doctorPermissionToRouteMap: Record<string, string> = {
  'doctor:home:menu': '/doctor/home',
  'doctor:consultation:chat:menu': '/doctor/consultation/chat',
  'doctor:consultation:menu': '/doctor/consultation',
  'doctor:patients:menu': '/doctor/patients',
}

/** 医生端菜单：权限编码 -> 图标名 */
export const doctorPermissionToIconMap: Record<string, string> = {
  'doctor:home:menu': 'HomeFilled',
  'doctor:consultation:chat:menu': 'ChatDotRound',
  'doctor:consultation:menu': 'Document',
  'doctor:patients:menu': 'UserFilled',
}

/**
 * 在权限树中按编码查找节点（递归）
 */
export function findPermissionNodeByCode(
  tree: UserPermissionTreeNode[],
  code: string
): UserPermissionTreeNode | null {
  for (const node of tree) {
    if (node.permissionCode === code) return node
    if (node.children?.length) {
      const found = findPermissionNodeByCode(node.children, code)
      if (found) return found
    }
  }
  return null
}

/** 患者端菜单项（用于顶部导航） */
export interface PatientNavItem {
  path: string
  title: string
  icon: string
  permissionCode: string
}

/**
 * 从权限树中解析患者端菜单（仅 type=1 且属于 patient-menu:group 的子节点）
 */
export function getPatientMenuItemsFromTree(
  tree: UserPermissionTreeNode[] | undefined
): PatientNavItem[] {
  if (!tree?.length) return []
  const group = findPermissionNodeByCode(tree, 'patient-menu:group')
  if (!group?.children?.length) return []
  const routeMap = patientPermissionToRouteMap
  const iconMap = patientPermissionToIconMap
  return group.children
    .filter((node) => node.permissionType === 1 && routeMap[node.permissionCode])
    .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
    .map((node) => ({
      path: routeMap[node.permissionCode],
      title: node.permissionName,
      icon: iconMap[node.permissionCode] ?? 'Document',
      permissionCode: node.permissionCode,
    }))
}

/** 医生端菜单项（用于侧边栏） */
export interface DoctorNavItem {
  path: string
  title: string
  icon: string
  permissionCode: string
}

/**
 * 从权限树中解析医生端菜单（仅 type=1 且属于 doctor-menu:group 的子节点）
 */
export function getDoctorMenuItemsFromTree(
  tree: UserPermissionTreeNode[] | undefined
): DoctorNavItem[] {
  if (!tree?.length) return []
  const group = findPermissionNodeByCode(tree, 'doctor-menu:group')
  if (!group?.children?.length) return []
  const routeMap = doctorPermissionToRouteMap
  const iconMap = doctorPermissionToIconMap
  return group.children
    .filter((node) => node.permissionType === 1 && routeMap[node.permissionCode])
    .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
    .map((node) => ({
      path: routeMap[node.permissionCode],
      title: node.permissionName,
      icon: iconMap[node.permissionCode] ?? 'Document',
      permissionCode: node.permissionCode,
    }))
}

export function convertPermissionTreeToMenuItems(
  tree: UserPermissionTreeNode[],
  permissionToRoute: Record<string, string> = permissionToRouteMap
): MenuItem[] {
  // 如果最上层只有一个节点且是"菜单权限分组"，则忽略它，直接使用其子节点
  let processedTree = tree
  if (
    tree.length === 1 &&
    tree[0]?.permissionCode === 'menu:group' &&
    tree[0]?.children &&
    tree[0].children.length > 0
  ) {
    processedTree = tree[0].children
  }

  return processedTree
    .filter((node) => {
      return node.permissionType === 1
    })
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
    .map((node) => {
      const route = permissionToRoute[node.permissionCode]
      
      let processedChildren: MenuItem[] = []
      if (node.children && node.children.length > 0) {
        const menuChildren = node.children.filter((child) => child.permissionType === 1)
        if (menuChildren.length > 0) {
          processedChildren = convertPermissionTreeToMenuItems(menuChildren, permissionToRoute)
        }
      }
      
      if (processedChildren.length > 0) {
        const subMenuIndex = `menu-${node.id}`
        return {
          index: subMenuIndex,
          title: node.permissionName,
          permissionCode: node.permissionCode,
          children: processedChildren,
        }
      } else {
        if (!route) {
          return null
        }
        return {
          index: route,
          title: node.permissionName,
          permissionCode: node.permissionCode,
        }
      }
    })
    .filter((item): item is MenuItem => item !== null)
}

