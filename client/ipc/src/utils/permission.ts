import type { PermissionTreeNode } from '../api/admin/permission'

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

export function convertPermissionTreeToMenuItems(
  tree: PermissionTreeNode[],
  permissionToRoute: Record<string, string> = permissionToRouteMap
): MenuItem[] {
  return tree
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

