-- ============================================
-- 知识库相关按钮权限及管理员角色关联
-- 1. 若不存在则插入知识库按钮权限（type=2）
-- 2. 为管理员角色（role_id=1）关联知识库按钮权限 + 知识库 API 权限
-- 依赖：knowledge:category:menu / knowledge:content:menu / knowledge:tag:menu 已存在
-- ============================================

-- 1. 知识库分类按钮权限（parent = knowledge:category:menu）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'knowledge:category:add',
    '新增知识库分类',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:category:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'knowledge:category:edit',
    '编辑知识库分类',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:category:menu' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'knowledge:category:delete',
    '删除知识库分类',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:category:menu' AND `is_deleted` = 0 LIMIT 1) t),
    2,
    0
),
(
    'knowledge:category:query',
    '查询知识库分类',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:category:menu' AND `is_deleted` = 0 LIMIT 1) t),
    3,
    0
);

-- 2. 知识库内容按钮权限（parent = knowledge:content:menu）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'knowledge:content:add',
    '新增知识库内容',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:content:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'knowledge:content:edit',
    '编辑知识库内容',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:content:menu' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'knowledge:content:delete',
    '删除知识库内容',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:content:menu' AND `is_deleted` = 0 LIMIT 1) t),
    2,
    0
),
(
    'knowledge:content:query',
    '查询知识库内容',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:content:menu' AND `is_deleted` = 0 LIMIT 1) t),
    3,
    0
);

-- 3. 知识库标签按钮权限（parent = knowledge:tag:menu）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'knowledge:tag:add',
    '新增知识库标签',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:tag:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'knowledge:tag:edit',
    '编辑知识库标签',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:tag:menu' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'knowledge:tag:delete',
    '删除知识库标签',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:tag:menu' AND `is_deleted` = 0 LIMIT 1) t),
    2,
    0
),
(
    'knowledge:tag:query',
    '查询知识库标签',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'knowledge:tag:menu' AND `is_deleted` = 0 LIMIT 1) t),
    3,
    0
);

-- 4. 为管理员角色（role_id=1）关联知识库按钮权限 + 知识库 API 权限（若未关联则插入）
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1 AS role_id, p.id AS permission_id
FROM `sys_permission` p
WHERE p.`is_deleted` = 0
  AND p.`permission_code` IN (
    -- 按钮权限
    'knowledge:category:add',
    'knowledge:category:edit',
    'knowledge:category:delete',
    'knowledge:category:query',
    'knowledge:content:add',
    'knowledge:content:edit',
    'knowledge:content:delete',
    'knowledge:content:query',
    'knowledge:tag:add',
    'knowledge:tag:edit',
    'knowledge:tag:delete',
    'knowledge:tag:query',
    -- API 权限（需已执行 replace_api_permissions.sql）
    'admin:api:knowledge:upload',
    'admin:api:knowledge-category:list',
    'admin:api:knowledge-category:detail',
    'admin:api:knowledge-category:create',
    'admin:api:knowledge-category:update',
    'admin:api:knowledge-category:delete',
    'admin:api:knowledge-content:list',
    'admin:api:knowledge-content:detail',
    'admin:api:knowledge-content:create',
    'admin:api:knowledge-content:update',
    'admin:api:knowledge-content:delete',
    'admin:api:knowledge-tag:list',
    'admin:api:knowledge-tag:detail',
    'admin:api:knowledge-tag:create',
    'admin:api:knowledge-tag:update',
    'admin:api:knowledge-tag:delete'
  );

-- 5. 可选：验证管理员角色下知识库相关权限数量
-- SELECT COUNT(*) FROM sys_role_permission rp
-- JOIN sys_permission p ON rp.permission_id = p.id
-- WHERE rp.role_id = 1 AND p.is_deleted = 0
--   AND (p.permission_code LIKE 'knowledge:%' OR p.permission_code LIKE 'admin:api:knowledge%');
