-- ============================================
-- 所有接口权限和路由权限分组数据插入
-- 为所有接口权限（permission_type=3）和路由权限（permission_type=4）创建分组节点（虚节点），用于组织管理
-- 虚节点不参与权限校验，仅用于组织结构
-- ============================================

-- 1. 创建所有接口权限的分组节点（虚节点）
-- 按照功能模块分组，每个分组节点作为该模块下所有接口权限的父节点
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
    -- 系统管理相关接口分组
    ('api:user:group', '用户管理接口分组', 3, 0, 1, 0),
    ('api:role:group', '角色管理接口分组', 3, 0, 2, 0),
    ('api:permission:group', '权限管理接口分组', 3, 0, 3, 0),
    ('api:file:group', '文件管理接口分组', 3, 0, 4, 0),
    ('api:access-log:group', '访问日志接口分组', 3, 0, 5, 0),
    ('api:operation-log:group', '操作日志接口分组', 3, 0, 6, 0),
    
    -- 用户信息相关接口分组
    ('api:patient-info:group', '患者信息接口分组', 3, 0, 7, 0),
    ('api:doctor-info:group', '医生信息接口分组', 3, 0, 8, 0),
    
    -- 问诊相关接口分组
    ('api:consultation-session:group', '问诊会话接口分组', 3, 0, 9, 0),
    ('api:consultation-message:group', '问诊消息接口分组', 3, 0, 10, 0),
    ('api:consultation-evaluation:group', '问诊评价接口分组', 3, 0, 11, 0),
    ('api:consultation:group', '问诊接口分组', 3, 0, 12, 0),
    
    -- 诊断相关接口分组
    ('api:skin-diagnosis-record:group', '皮肤诊断记录接口分组', 3, 0, 13, 0),
    ('api:diagnosis-result:group', '诊断结果接口分组', 3, 0, 14, 0),
    ('api:disease-type:group', '疾病类型接口分组', 3, 0, 15, 0),
    ('api:diagnosis:group', '诊断接口分组', 3, 0, 16, 0),
    
    -- 知识库相关接口分组
    ('api:knowledge-category:group', '知识库分类接口分组', 3, 0, 17, 0),
    ('api:knowledge-content:group', '知识库内容接口分组', 3, 0, 18, 0),
    ('api:knowledge-tag:group', '知识库标签接口分组', 3, 0, 19, 0),
    
    -- 统计相关接口分组
    ('api:statistics-daily:group', '每日统计接口分组', 3, 0, 20, 0),
    
    -- 患者相关接口分组
    ('api:patient:group', '患者接口分组', 3, 0, 21, 0),
    
    -- 路由权限分组（permission_type=4）
    ('patient:group', '患者路由分组', 4, 0, 1, 0),
    ('doctor:group', '医生路由分组', 4, 0, 2, 0),
    ('admin:group', '管理员路由分组', 4, 0, 3, 0);

-- 2. 更新用户管理接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:user:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:user:%'
AND p1.permission_code != 'api:user:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 3. 更新角色管理接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:role:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:role:%'
AND p1.permission_code != 'api:role:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 4. 更新权限管理接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:permission:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:permission:%'
AND p1.permission_code != 'api:permission:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 5. 更新文件管理接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:file:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:file:%'
AND p1.permission_code != 'api:file:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 6. 更新访问日志接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:access-log:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:access-log:%'
AND p1.permission_code != 'api:access-log:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 7. 更新操作日志接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:operation-log:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:operation-log:%'
AND p1.permission_code != 'api:operation-log:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 8. 更新患者信息接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:patient-info:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:patient-info:%'
AND p1.permission_code != 'api:patient-info:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 9. 更新医生信息接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:doctor-info:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:doctor-info:%'
AND p1.permission_code != 'api:doctor-info:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 10. 更新问诊会话接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:consultation-session:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:consultation-session:%'
AND p1.permission_code != 'api:consultation-session:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 11. 更新问诊消息接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:consultation-message:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:consultation-message:%'
AND p1.permission_code != 'api:consultation-message:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 12. 更新问诊评价接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:consultation-evaluation:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:consultation-evaluation:%'
AND p1.permission_code != 'api:consultation-evaluation:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 13. 更新问诊接口权限的parent_id（通用问诊接口）
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:consultation:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:consultation:%'
AND p1.permission_code NOT LIKE 'api:consultation-session:%'
AND p1.permission_code NOT LIKE 'api:consultation-message:%'
AND p1.permission_code NOT LIKE 'api:consultation-evaluation:%'
AND p1.permission_code != 'api:consultation:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 14. 更新皮肤诊断记录接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:skin-diagnosis-record:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:skin-diagnosis-record:%'
AND p1.permission_code != 'api:skin-diagnosis-record:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 15. 更新诊断结果接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:diagnosis-result:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:diagnosis-result:%'
AND p1.permission_code != 'api:diagnosis-result:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 16. 更新疾病类型接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:disease-type:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:disease-type:%'
AND p1.permission_code != 'api:disease-type:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 17. 更新诊断接口权限的parent_id（通用诊断接口）
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:diagnosis:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:diagnosis:%'
AND p1.permission_code NOT LIKE 'api:skin-diagnosis-record:%'
AND p1.permission_code NOT LIKE 'api:diagnosis-result:%'
AND p1.permission_code NOT LIKE 'api:disease-type:%'
AND p1.permission_code != 'api:diagnosis:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 18. 更新知识库分类接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:knowledge-category:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:knowledge-category:%'
AND p1.permission_code != 'api:knowledge-category:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 19. 更新知识库内容接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:knowledge-content:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:knowledge-content:%'
AND p1.permission_code != 'api:knowledge-content:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 20. 更新知识库标签接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:knowledge-tag:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:knowledge-tag:%'
AND p1.permission_code != 'api:knowledge-tag:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 21. 更新每日统计接口权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:statistics-daily:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:statistics-daily:%'
AND p1.permission_code != 'api:statistics-daily:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 22. 更新患者接口权限的parent_id（通用患者接口）
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'api:patient:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'api:patient:%'
AND p1.permission_code NOT LIKE 'api:patient-info:%'
AND p1.permission_code != 'api:patient:group'
AND p1.permission_type = 3
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 23. 更新患者路由权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'patient:%'
AND p1.permission_code != 'patient:group'
AND p1.permission_type = 4
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 24. 更新医生路由权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'doctor:%'
AND p1.permission_code != 'doctor:group'
AND p1.permission_type = 4
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 25. 更新管理员路由权限的parent_id
UPDATE `sys_permission` p1
INNER JOIN (
    SELECT id FROM `sys_permission` WHERE `permission_code` = 'admin:group' AND `is_deleted` = 0 LIMIT 1
) p2
SET p1.parent_id = p2.id
WHERE p1.permission_code LIKE 'admin:%'
AND p1.permission_code != 'admin:group'
AND p1.permission_type = 4
AND p1.is_deleted = 0
AND (p1.parent_id = 0 OR p1.parent_id IS NULL OR p1.parent_id != p2.id);

-- 26. 将所有分组节点和接口权限、路由权限关联到管理员角色（role_id = 1）
-- 注意：分组节点作为虚节点，虽然关联到角色，但在权限校验时不会被检查
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT
    1 AS role_id,
    p.id AS permission_id
FROM `sys_permission` p
WHERE (
    -- 所有接口权限分组节点
    p.permission_code IN (
        'api:user:group',
        'api:role:group',
        'api:permission:group',
        'api:file:group',
        'api:access-log:group',
        'api:operation-log:group',
        'api:patient-info:group',
        'api:doctor-info:group',
        'api:consultation-session:group',
        'api:consultation-message:group',
        'api:consultation-evaluation:group',
        'api:consultation:group',
        'api:skin-diagnosis-record:group',
        'api:diagnosis-result:group',
        'api:disease-type:group',
        'api:diagnosis:group',
        'api:knowledge-category:group',
        'api:knowledge-content:group',
        'api:knowledge-tag:group',
        'api:statistics-daily:group',
        'api:patient:group'
    )
    -- 所有路由权限分组节点
    OR p.permission_code IN (
        'patient:group',
        'doctor:group',
        'admin:group'
    )
    -- 或者所有接口权限（permission_type=3且不是分组节点）
    OR (p.permission_type = 3 
        AND p.permission_code NOT LIKE '%:group'
        AND p.is_deleted = 0)
    -- 或者所有路由权限（permission_type=4且不是分组节点）
    OR (p.permission_type = 4 
        AND p.permission_code NOT LIKE '%:group'
        AND p.is_deleted = 0)
)
AND p.is_deleted = 0
AND NOT EXISTS (
    SELECT 1 
    FROM `sys_role_permission` rp 
    WHERE rp.role_id = 1 
    AND rp.permission_id = p.id
);

-- 27. 验证：查询所有接口权限分组
-- SELECT * FROM sys_permission WHERE permission_code LIKE '%:group' AND permission_type = 3 ORDER BY sort;

-- 28. 验证：查询所有路由权限分组
-- SELECT * FROM sys_permission WHERE permission_code LIKE '%:group' AND permission_type = 4 ORDER BY sort;

-- 29. 验证：查询所有接口权限及其分组关系
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- WHERE p1.permission_type = 3 AND p1.is_deleted = 0
-- ORDER BY COALESCE(p1.parent_id, p1.id), p1.sort;

-- 30. 验证：查询所有路由权限及其分组关系
-- SELECT 
--     p1.id, p1.permission_code, p1.permission_name, p1.parent_id, p1.sort,
--     p2.permission_code AS parent_code, p2.permission_name AS parent_name
-- FROM sys_permission p1
-- LEFT JOIN sys_permission p2 ON p1.parent_id = p2.id
-- WHERE p1.permission_type = 4 AND p1.is_deleted = 0
-- ORDER BY COALESCE(p1.parent_id, p1.id), p1.sort;

-- 31. 验证：统计每个分组的接口权限数量
-- SELECT 
--     p2.permission_code AS group_code,
--     p2.permission_name AS group_name,
--     COUNT(p1.id) AS permission_count
-- FROM sys_permission p2
-- LEFT JOIN sys_permission p1 ON p1.parent_id = p2.id AND p1.is_deleted = 0
-- WHERE p2.permission_code LIKE '%:group' AND p2.permission_type = 3 AND p2.is_deleted = 0
-- GROUP BY p2.id, p2.permission_code, p2.permission_name
-- ORDER BY p2.sort;

-- 32. 验证：统计每个分组的路由权限数量
-- SELECT 
--     p2.permission_code AS group_code,
--     p2.permission_name AS group_name,
--     COUNT(p1.id) AS permission_count
-- FROM sys_permission p2
-- LEFT JOIN sys_permission p1 ON p1.parent_id = p2.id AND p1.is_deleted = 0
-- WHERE p2.permission_code LIKE '%:group' AND p2.permission_type = 4 AND p2.is_deleted = 0
-- GROUP BY p2.id, p2.permission_code, p2.permission_name
-- ORDER BY p2.sort;

