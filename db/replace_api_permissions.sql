-- ============================================
-- 接口权限重构：管理员/患者/医生 API 互不重叠
-- permission_code 前缀：admin:api:* / patient:api:* / doctor:api:*
-- 执行前请备份 sys_permission、sys_role_permission
-- ============================================

-- 1. 删除角色-接口权限关联
DELETE rp FROM sys_role_permission rp
INNER JOIN sys_permission p ON rp.permission_id = p.id
WHERE p.permission_type = 3 AND p.is_deleted = 0;

-- 2. 删除所有接口权限（type=3）
DELETE FROM sys_permission WHERE permission_type = 3;

-- 3. 插入三个接口权限分组（parent_id=0）
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, sort, is_deleted) VALUES
('api:admin:group', '管理员接口权限分组', 3, 0, 0, 0),
('api:patient:group', '患者接口权限分组', 3, 0, 1, 0),
('api:doctor:group', '医生接口权限分组', 3, 0, 2, 0);

-- 4. 管理员接口权限（admin:api:*）：派生表 + CROSS JOIN 父级 id，避免 UNION ALL 多 FROM 语法问题
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, sort, is_deleted)
SELECT d.code, d.name, 3, p.id, d.sort, 0
FROM (
  SELECT 'admin:api:user:list' AS code, '用户列表接口' AS name, 0 AS sort
  UNION ALL SELECT 'admin:api:user:detail', '用户详情接口', 1
  UNION ALL SELECT 'admin:api:user:create', '创建用户接口', 2
  UNION ALL SELECT 'admin:api:user:update', '更新用户接口', 3
  UNION ALL SELECT 'admin:api:user:delete', '删除用户接口', 4
  UNION ALL SELECT 'admin:api:role:list', '角色列表接口', 5
  UNION ALL SELECT 'admin:api:role:detail', '角色详情接口', 6
  UNION ALL SELECT 'admin:api:role:create', '创建角色接口', 7
  UNION ALL SELECT 'admin:api:role:update', '更新角色接口', 8
  UNION ALL SELECT 'admin:api:role:delete', '删除角色接口', 9
  UNION ALL SELECT 'admin:api:permission:list', '权限列表接口', 10
  UNION ALL SELECT 'admin:api:permission:detail', '权限详情接口', 11
  UNION ALL SELECT 'admin:api:permission:create', '创建权限接口', 12
  UNION ALL SELECT 'admin:api:permission:update', '更新权限接口', 13
  UNION ALL SELECT 'admin:api:permission:delete', '删除权限接口', 14
  UNION ALL SELECT 'admin:api:access-log:list', '访问日志列表接口', 15
  UNION ALL SELECT 'admin:api:access-log:detail', '访问日志详情接口', 16
  UNION ALL SELECT 'admin:api:access-log:delete', '删除访问日志接口', 17
  UNION ALL SELECT 'admin:api:operation-log:list', '操作日志列表接口', 18
  UNION ALL SELECT 'admin:api:operation-log:detail', '操作日志详情接口', 19
  UNION ALL SELECT 'admin:api:operation-log:delete', '删除操作日志接口', 20
  UNION ALL SELECT 'admin:api:consultation-session:list', '问诊会话列表接口', 21
  UNION ALL SELECT 'admin:api:consultation-session:detail', '问诊会话详情接口', 22
  UNION ALL SELECT 'admin:api:consultation-session:update', '更新问诊会话接口', 23
  UNION ALL SELECT 'admin:api:consultation-session:delete', '删除问诊会话接口', 24
  UNION ALL SELECT 'admin:api:consultation-message:list', '问诊消息列表接口', 25
  UNION ALL SELECT 'admin:api:consultation-message:delete', '删除问诊消息接口', 26
  UNION ALL SELECT 'admin:api:consultation-evaluation:list', '问诊评价列表接口', 27
  UNION ALL SELECT 'admin:api:consultation-evaluation:create', '创建问诊评价接口', 28
  UNION ALL SELECT 'admin:api:consultation-evaluation:update', '更新问诊评价接口', 29
  UNION ALL SELECT 'admin:api:consultation-evaluation:delete', '删除问诊评价接口', 30
  UNION ALL SELECT 'admin:api:file:list', '文件列表接口', 31
  UNION ALL SELECT 'admin:api:file:detail', '文件详情接口', 32
  UNION ALL SELECT 'admin:api:file:delete', '删除文件接口', 33
) AS d
CROSS JOIN (SELECT id FROM sys_permission WHERE permission_code = 'api:admin:group' AND is_deleted = 0 LIMIT 1) AS p;

-- 5. 患者接口权限（patient:api:*）
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, sort, is_deleted)
SELECT d.code, d.name, 3, p.id, d.sort, 0
FROM (
  SELECT 'patient:api:consultation-session:list' AS code, '问诊会话列表接口' AS name, 0 AS sort
  UNION ALL SELECT 'patient:api:consultation-session:detail', '问诊会话详情接口', 1
  UNION ALL SELECT 'patient:api:consultation-session:create', '创建问诊会话接口', 2
  UNION ALL SELECT 'patient:api:consultation-session:update', '更新问诊会话接口', 3
  UNION ALL SELECT 'patient:api:consultation-message:list', '问诊消息列表接口', 4
  UNION ALL SELECT 'patient:api:consultation-message:create', '创建问诊消息接口', 5
  UNION ALL SELECT 'patient:api:consultation-message:update', '更新问诊消息接口', 6
  UNION ALL SELECT 'patient:api:diagnosis:upload', '上传诊断图片接口', 7
  UNION ALL SELECT 'patient:api:diagnosis:result', '获取诊断结果接口', 8
  UNION ALL SELECT 'patient:api:skin-diagnosis-record:list', '皮肤诊断记录列表接口', 9
  UNION ALL SELECT 'patient:api:skin-diagnosis-record:detail', '皮肤诊断记录详情接口', 10
  UNION ALL SELECT 'patient:api:doctor-info:list', '医生信息列表接口', 11
) AS d
CROSS JOIN (SELECT id FROM sys_permission WHERE permission_code = 'api:patient:group' AND is_deleted = 0 LIMIT 1) AS p;

-- 6. 医生接口权限（doctor:api:*）
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, sort, is_deleted)
SELECT d.code, d.name, 3, p.id, d.sort, 0
FROM (
  SELECT 'doctor:api:doctor-info:list' AS code, '医生信息列表接口' AS name, 0 AS sort
  UNION ALL SELECT 'doctor:api:consultation-message:list', '问诊消息列表接口', 1
  UNION ALL SELECT 'doctor:api:consultation-message:create', '创建问诊消息接口', 2
  UNION ALL SELECT 'doctor:api:consultation-message:update', '更新问诊消息接口', 3
  UNION ALL SELECT 'doctor:api:consultation-session:update', '更新问诊会话接口', 4
) AS d
CROSS JOIN (SELECT id FROM sys_permission WHERE permission_code = 'api:doctor:group' AND is_deleted = 0 LIMIT 1) AS p;
