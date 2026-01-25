-- ============================================
-- 权限类型分组数据插入
-- 为路由、菜单、按钮、接口权限分别创建顶级分组节点（虚节点），用于组织管理
-- 虚节点不参与权限校验，仅用于组织结构
-- ============================================

-- 1. 创建权限类型分组节点（虚节点）
-- 每个权限类型创建一个顶级分组节点，作为该类型下所有权限的父节点
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
    -- 菜单权限分组（permission_type=1）
    ('menu:group', '菜单权限分组', 1, 0, 1, 0),
    
    -- 按钮权限分组（permission_type=2）
    ('button:group', '按钮权限分组', 2, 0, 1, 0),
    
    -- 接口权限分组（permission_type=3）
    ('api:group', '接口权限分组', 3, 0, 1, 0),
    
    -- 路由权限分组（permission_type=4）
    ('route:group', '路由权限分组', 4, 0, 1, 0);

-- 2. 更新没有父节点的菜单权限的parent_id（permission_type=1）
-- 只将parent_id=0或NULL的菜单权限关联到菜单权限分组节点，已有父节点的保持不变
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'menu:group' AND `permission_type` = 1 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 1
AND p1.permission_code != 'menu:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 3. 更新没有父节点的按钮权限的parent_id（permission_type=2）
-- 只将parent_id=0或NULL的按钮权限关联到按钮权限分组节点，已有父节点的保持不变
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'button:group' AND `permission_type` = 2 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 2
AND p1.permission_code != 'button:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 4. 更新没有父节点的接口权限功能分组节点的parent_id（permission_type=3）
-- 只将parent_id=0或NULL的接口权限功能分组（如api:user:group）关联到接口权限顶级分组节点（api:group），已有父节点的保持不变
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:group' AND `permission_type` = 3 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 3
AND p1.permission_code LIKE '%:group'
AND p1.permission_code != 'api:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 5. 更新所有未分组的接口权限的parent_id（permission_type=3）
-- 将没有功能分组的接口权限直接关联到接口权限顶级分组节点
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:group' AND `permission_type` = 3 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 3
AND p1.permission_code NOT LIKE '%:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 6. 更新没有父节点的路由权限角色分组节点的parent_id（permission_type=4）
-- 只将parent_id=0或NULL的路由权限角色分组（如patient:group）关联到路由权限顶级分组节点（route:group），已有父节点的保持不变
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'route:group' AND `permission_type` = 4 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 4
AND p1.permission_code LIKE '%:group'
AND p1.permission_code != 'route:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 7. 更新所有未分组的路由权限的parent_id（permission_type=4）
-- 将没有角色分组的路由权限直接关联到路由权限顶级分组节点
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'route:group' AND `permission_type` = 4 AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_type = 4
AND p1.permission_code NOT LIKE '%:group'
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL);

-- 8. 将权限类型分组节点关联到管理员角色（role_id = 1）
-- 注意：分组节点作为虚节点，虽然关联到角色，但在权限校验时不会被检查
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT
    1 AS role_id,
    p.id AS permission_id
FROM `sys_permission` p
WHERE p.permission_code IN (
    'menu:group',
    'button:group',
    'api:group',
    'route:group'
)
AND p.is_deleted = 0
AND NOT EXISTS (
    SELECT 1 
    FROM `sys_role_permission` rp 
    WHERE rp.role_id = 1 
    AND rp.permission_id = p.id
);

-- 9. 验证：查询所有权限类型分组节点
-- SELECT * FROM sys_permission WHERE permission_code IN ('menu:group', 'button:group', 'api:group', 'route:group') ORDER BY permission_type;

-- 10. 验证：统计每种权限类型的数量
-- SELECT 
--     permission_type,
--     CASE permission_type
--         WHEN 1 THEN '菜单权限'
--         WHEN 2 THEN '按钮权限'
--         WHEN 3 THEN '接口权限'
--         WHEN 4 THEN '路由权限'
--         ELSE '未知类型'
--     END AS type_name,
--     COUNT(*) AS total_count,
--     SUM(CASE WHEN parent_id = 0 OR parent_id IS NULL THEN 1 ELSE 0 END) AS ungrouped_count
-- FROM sys_permission
-- WHERE is_deleted = 0
-- GROUP BY permission_type
-- ORDER BY permission_type;

-- 11. 验证：查询菜单权限及其分组关系
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- WHERE p1.permission_type = 1 AND p1.is_deleted = 0
-- ORDER BY COALESCE(p1.parent_id, p1.id), p1.sort
-- LIMIT 20;

-- 12. 验证：查询按钮权限及其分组关系
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- WHERE p1.permission_type = 2 AND p1.is_deleted = 0
-- ORDER BY COALESCE(p1.parent_id, p1.id), p1.sort
-- LIMIT 20;

-- 13. 验证：查询接口权限及其分组关系（显示两级分组：类型分组 -> 功能分组）
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name,
--     p3.permission_code AS grandparent_code, p3.permission_name AS grandparent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- LEFT JOIN sys_permission p3 ON p2.parent_id = p3.id
-- WHERE p1.permission_type = 3 AND p1.is_deleted = 0 AND p1.permission_code NOT LIKE '%:group'
-- ORDER BY COALESCE(p2.parent_id, p1.parent_id, p1.id), p1.sort
-- LIMIT 20;

-- 14. 验证：查询路由权限及其分组关系（显示两级分组：类型分组 -> 角色分组）
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name,
--     p3.permission_code AS grandparent_code, p3.permission_name AS grandparent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- LEFT JOIN sys_permission p3 ON p2.parent_id = p3.id
-- WHERE p1.permission_type = 4 AND p1.is_deleted = 0 AND p1.permission_code NOT LIKE '%:group'
-- ORDER BY COALESCE(p2.parent_id, p1.parent_id, p1.id), p1.sort
-- LIMIT 20;

