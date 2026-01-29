/**
 * 管理员API统一导出
 * 各模块API已拆分到独立文件，这里统一导出以保持向后兼容
 */

// 用户管理
export * from './user'

// 角色管理
export * from './role'

// 权限管理
export * from './permission'

// 访问日志管理
export * from './accessLog'

// 操作日志管理
export * from './operationLog'

// 问诊管理
export * from './consultation'

// 文件管理
export * from './file'

// 知识库管理（分类、内容、标签、图片上传）
export * from './knowledge'

// 管理员首页（数据概览等）
export * from './home'
