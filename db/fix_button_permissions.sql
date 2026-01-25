-- ============================================
-- 修复按钮权限的 parent_id，确保按钮权限正确挂载到菜单权限下
-- ============================================

-- 1. 添加缺失的按钮权限

-- 角色管理相关按钮权限
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `sort`, `is_deleted`)
VALUES ('system:role:assign', '分配权限', 2, 8, 3, 0)
ON DUPLICATE KEY UPDATE `parent_id` = 8, `sort` = 3;

-- 用户管理相关按钮权限
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `sort`, `is_deleted`)
VALUES ('system:user:reset-password', '重置密码', 2, 7, 4, 0)
ON DUPLICATE KEY UPDATE `parent_id` = 7, `sort` = 4;

-- 访问日志相关按钮权限
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `sort`, `is_deleted`)
VALUES 
('system:access-log:view', '查看访问日志详情', 2, 48, 2, 0),
('system:access-log:batch-delete', '批量删除访问日志', 2, 48, 3, 0)
ON DUPLICATE KEY UPDATE `parent_id` = VALUES(`parent_id`), `sort` = VALUES(`sort`);

-- 操作日志相关按钮权限
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `sort`, `is_deleted`)
VALUES 
('system:operation-log:view', '查看操作日志详情', 2, 49, 2, 0),
('system:operation-log:batch-delete', '批量删除操作日志', 2, 49, 3, 0)
ON DUPLICATE KEY UPDATE `parent_id` = VALUES(`parent_id`), `sort` = VALUES(`sort`);

-- 2. 修复现有按钮权限的 parent_id（确保所有按钮权限都正确挂载到菜单权限下）

-- 系统用户管理按钮权限 (parent_id 应该指向 system:user:menu, ID=7)
UPDATE `sys_permission` 
SET `parent_id` = 7 
WHERE `permission_type` = 2 
  AND `permission_code` IN ('system:user:add', 'system:user:edit', 'system:user:delete', 'system:user:query', 'system:user:reset-password')
  AND `parent_id` != 7;

-- 系统角色管理按钮权限 (parent_id 应该指向 system:role:menu, ID=8)
UPDATE `sys_permission` 
SET `parent_id` = 8 
WHERE `permission_type` = 2 
  AND `permission_code` IN ('system:role:add', 'system:role:edit', 'system:role:delete', 'system:role:assign')
  AND `parent_id` != 8;

-- 系统权限管理按钮权限 (parent_id 应该指向 system:permission:menu, ID=9)
UPDATE `sys_permission` 
SET `parent_id` = 9 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'system:permission:%'
  AND `parent_id` != 9;

-- 文件管理按钮权限 (parent_id 应该指向 system:file:menu, ID=47)
UPDATE `sys_permission` 
SET `parent_id` = 47 
WHERE `permission_type` = 2 
  AND `permission_code` IN ('system:file:add', 'system:file:edit', 'system:file:delete', 'system:file:query')
  AND `parent_id` != 47;

-- 访问日志按钮权限 (parent_id 应该指向 system:access-log:menu, ID=48)
UPDATE `sys_permission` 
SET `parent_id` = 48 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'system:access-log:%'
  AND `parent_id` != 48;

-- 操作日志按钮权限 (parent_id 应该指向 system:operation-log:menu, ID=49)
UPDATE `sys_permission` 
SET `parent_id` = 49 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'system:operation-log:%'
  AND `parent_id` != 49;

-- 患者列表按钮权限 (parent_id 应该指向 patient:list:menu, ID=10)
UPDATE `sys_permission` 
SET `parent_id` = 10 
WHERE `permission_type` = 2 
  AND `permission_code` IN ('patient:add', 'patient:edit', 'patient:delete')
  AND `parent_id` != 10;

-- 用户信息管理 - 患者信息按钮权限 (parent_id 应该指向 user-info:patient:menu, ID=51)
UPDATE `sys_permission` 
SET `parent_id` = 51 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'user-info:patient:%'
  AND `parent_id` != 51;

-- 用户信息管理 - 医生信息按钮权限 (parent_id 应该指向 user-info:doctor:menu, ID=52)
UPDATE `sys_permission` 
SET `parent_id` = 52 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'user-info:doctor:%'
  AND `parent_id` != 52;

-- 问诊会话按钮权限 (parent_id 应该指向 consultation:session:menu, ID=53)
UPDATE `sys_permission` 
SET `parent_id` = 53 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'consultation:session:%'
  AND `parent_id` != 53;

-- 问诊消息按钮权限 (parent_id 应该指向 consultation:message:menu, ID=54)
UPDATE `sys_permission` 
SET `parent_id` = 54 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'consultation:message:%'
  AND `parent_id` != 54;

-- 问诊评价按钮权限 (parent_id 应该指向 consultation:evaluation:menu, ID=55)
UPDATE `sys_permission` 
SET `parent_id` = 55 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'consultation:evaluation:%'
  AND `parent_id` != 55;

-- 皮肤诊断记录按钮权限 (parent_id 应该指向 diagnosis:record:menu, ID=56)
UPDATE `sys_permission` 
SET `parent_id` = 56 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'diagnosis:record:%'
  AND `parent_id` != 56;

-- 诊断结果按钮权限 (parent_id 应该指向 diagnosis:result:menu, ID=57)
UPDATE `sys_permission` 
SET `parent_id` = 57 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'diagnosis:result:%'
  AND `parent_id` != 57;

-- 疾病类型按钮权限 (parent_id 应该指向 diagnosis:disease-type:menu, ID=58)
UPDATE `sys_permission` 
SET `parent_id` = 58 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'diagnosis:disease-type:%'
  AND `parent_id` != 58;

-- 知识库分类按钮权限 (parent_id 应该指向 knowledge:category:menu, ID=59)
UPDATE `sys_permission` 
SET `parent_id` = 59 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'knowledge:category:%'
  AND `parent_id` != 59;

-- 知识库内容按钮权限 (parent_id 应该指向 knowledge:content:menu, ID=60)
UPDATE `sys_permission` 
SET `parent_id` = 60 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'knowledge:content:%'
  AND `parent_id` != 60;

-- 知识库标签按钮权限 (parent_id 应该指向 knowledge:tag:menu, ID=61)
UPDATE `sys_permission` 
SET `parent_id` = 61 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'knowledge:tag:%'
  AND `parent_id` != 61;

-- 每日统计按钮权限 (parent_id 应该指向 statistics:daily:menu, ID=63)
UPDATE `sys_permission` 
SET `parent_id` = 63 
WHERE `permission_type` = 2 
  AND `permission_code` LIKE 'statistics:daily:%'
  AND `parent_id` != 63;

-- 3. 验证：查询所有按钮权限，检查是否有 parent_id 指向非菜单权限的记录
-- SELECT p.id, p.permission_code, p.permission_name, p.parent_id, 
--        parent.permission_code as parent_code, parent.permission_type as parent_type
-- FROM sys_permission p
-- LEFT JOIN sys_permission parent ON p.parent_id = parent.id
-- WHERE p.permission_type = 2 
--   AND (parent.permission_type IS NULL OR parent.permission_type != 1)
--   AND p.is_deleted = 0;

