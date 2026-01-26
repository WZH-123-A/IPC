import request, { type ApiResponse } from './request'

// 用户权限树节点接口
export interface UserPermissionTreeNode {
  id: number
  permissionCode: string
  permissionName: string
  permissionType: number
  parentId: number
  sort: number
  children?: UserPermissionTreeNode[]
}

/**
 * 从权限树中提取所有权限编码的辅助函数
 * @param permissions 权限树节点数组
 * @returns 权限编码数组
 */
export function extractPermissionCodes(permissions: UserPermissionTreeNode[]): string[] {
  const codes: string[] = []
  const traverse = (nodes: UserPermissionTreeNode[]) => {
    for (const node of nodes) {
      codes.push(node.permissionCode)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  traverse(permissions)
  return codes
}

/**
 * 从权限树中提取指定类型的权限编码
 * @param permissions 权限树节点数组
 * @param permissionType 权限类型：1-菜单 2-按钮 3-接口 4-路由
 * @returns 权限编码数组
 */
export function extractPermissionCodesByType(
  permissions: UserPermissionTreeNode[],
  permissionType: number
): string[] {
  const codes: string[] = []
  const traverse = (nodes: UserPermissionTreeNode[]) => {
    for (const node of nodes) {
      if (node.permissionType === permissionType) {
        codes.push(node.permissionCode)
      }
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  traverse(permissions)
  return codes
}

/**
 * 从权限树中过滤出指定类型的权限树
 * @param tree 权限树节点数组
 * @param permissionType 权限类型：1-菜单 2-按钮 3-接口 4-路由
 * @returns 过滤后的权限树（只包含指定类型的节点）
 */
export function filterPermissionsByType(
  tree: UserPermissionTreeNode[],
  permissionType: number
): UserPermissionTreeNode[] {
  const traverse = (nodes: UserPermissionTreeNode[]): UserPermissionTreeNode[] => {
    return nodes
      .filter((node) => node.permissionType === permissionType) // 只保留指定类型的权限
      .map((node) => {
        const filteredNode: UserPermissionTreeNode = {
          ...node,
          children: node.children && node.children.length > 0 
            ? traverse(node.children) 
            : undefined,
        }
        return filteredNode
      })
  }

  return traverse(tree)
}

/**
 * 获取当前用户的菜单权限树
 */
export const getCurrentUserMenusApi = async (): Promise<UserPermissionTreeNode[]> => {
  const response = await request.get<ApiResponse<UserPermissionTreeNode[]>>('/auth/menus')
  return response.data.data
}

/**
 * 获取当前用户的按钮权限树
 */
export const getCurrentUserButtonsApi = async (): Promise<UserPermissionTreeNode[]> => {
  const response = await request.get<ApiResponse<UserPermissionTreeNode[]>>('/auth/buttons')
  return response.data.data
}

/**
 * 获取当前用户的路由权限树
 */
export const getCurrentUserRoutesApi = async (): Promise<UserPermissionTreeNode[]> => {
  const response = await request.get<ApiResponse<UserPermissionTreeNode[]>>('/auth/routes')
  return response.data.data
}

/**
 * 获取当前用户的接口权限树
 */
export const getCurrentUserApisApi = async (): Promise<UserPermissionTreeNode[]> => {
  const response = await request.get<ApiResponse<UserPermissionTreeNode[]>>('/auth/apis')
  return response.data.data
}

