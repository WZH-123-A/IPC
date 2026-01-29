-- ============================================
-- 根据新的 sys_permission（接口权限）更新 sys_role_permission
-- 角色：1=管理员(admin), 2=医生(doctor), 4=患者(patient)
-- 执行顺序：先执行 replace_api_permissions.sql，再执行本脚本
-- 执行前请备份 sys_role_permission
-- ============================================

-- 1. 删除与接口权限（permission_type=3）相关的角色-权限关联
DELETE rp FROM sys_role_permission rp
INNER JOIN sys_permission p ON rp.permission_id = p.id
WHERE p.permission_type = 3 AND p.is_deleted = 0;

-- 2. 为管理员角色(role_id=1)分配所有管理员接口权限（api:admin:group 及 admin:api:*）
INSERT INTO sys_role_permission (role_id, permission_id, create_time)
SELECT 1, id, NOW() FROM sys_permission
WHERE (permission_code = 'api:admin:group' OR permission_code LIKE 'admin:api:%')
  AND is_deleted = 0;

-- 3. 为医生角色(role_id=2)分配所有医生接口权限（api:doctor:group 及 doctor:api:*）
INSERT INTO sys_role_permission (role_id, permission_id, create_time)
SELECT 2, id, NOW() FROM sys_permission
WHERE (permission_code = 'api:doctor:group' OR permission_code LIKE 'doctor:api:%')
  AND is_deleted = 0;

-- 4. 为患者角色(role_id=4)分配所有患者接口权限（api:patient:group 及 patient:api:*）
INSERT INTO sys_role_permission (role_id, permission_id, create_time)
SELECT 4, id, NOW() FROM sys_permission
WHERE (permission_code = 'api:patient:group' OR permission_code LIKE 'patient:api:%')
  AND is_deleted = 0;
