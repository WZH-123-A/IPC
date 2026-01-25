-- ============================================
-- 权限管理控制器权限数据插入
-- 向 sys_permission 和 sys_role_permission 表中添加权限管理相关的权限
-- 使用分组结构：创建父权限节点（虚节点）用于组织管理
-- ============================================

-- 1. 首先创建父权限分组节点（虚节点，不参与权限校验，仅用于组织结构）
-- 使用接口权限类型，但作为分组节点，parent_id=0表示顶级，sort=14与其他接口权限分组保持一致
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
    ('api:permission:group', '权限管理接口分组', 3, 0, 14, 0);

-- 2. 插入权限管理接口权限数据到 sys_permission 表
-- 先插入权限，parent_id暂时设为0，后续再更新
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
    ('api:permission:list', '权限列表查询', 3, 0, 1, 0),
    ('api:permission:detail', '权限详情查询', 3, 0, 2, 0),
    ('api:permission:create', '新增权限', 3, 0, 3, 0),
    ('api:permission:update', '更新权限', 3, 0, 4, 0),
    ('api:permission:delete', '删除权限', 3, 0, 5, 0);

-- 3. 更新已存在的权限的parent_id（如果它们还没有正确的父节点）
-- 确保所有权限管理接口权限都关联到父节点
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:permission:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code IN (
    'api:permission:list',
    'api:permission:detail',
    'api:permission:create',
    'api:permission:update',
    'api:permission:delete'
)
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 4. 将权限管理相关的所有权限（包括父节点和子权限）关联到管理员角色（role_id = 1）
-- 注意：父节点作为虚节点，虽然关联到角色，但在权限校验时不会被检查
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT
    1 AS role_id,
    p.id AS permission_id
FROM `sys_permission` p
WHERE (
    p.permission_code = 'api:permission:group'
    OR p.permission_code IN (
        'api:permission:list',
        'api:permission:detail',
        'api:permission:create',
        'api:permission:update',
        'api:permission:delete'
    )
)
AND p.is_deleted = 0
AND NOT EXISTS (
    SELECT 1 
    FROM `sys_role_permission` rp 
    WHERE rp.role_id = 1 
    AND rp.permission_id = p.id
);

-- 5. 验证：查询插入的权限（包括父节点和子权限）
-- SELECT * FROM sys_permission WHERE permission_code LIKE 'api:permission:%' ORDER BY parent_id, sort;

-- 6. 验证：查询权限树结构
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- WHERE p1.permission_code LIKE 'api:permission:%' AND p1.is_deleted = 0
-- ORDER BY COALESCE(p1.parent_id, p1.id), p1.sort;

-- 7. 验证：查询管理员角色关联的权限管理权限
-- SELECT rp.*, p.permission_code, p.permission_name, p.parent_id
-- FROM sys_role_permission rp
-- INNER JOIN sys_permission p ON rp.permission_id = p.id
-- WHERE rp.role_id = 1 AND p.permission_code LIKE 'api:permission:%'
-- ORDER BY p.parent_id, p.sort;

