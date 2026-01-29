-- ============================================
-- 患者菜单权限分组、医生菜单权限分组及子菜单
-- 患者/医生菜单分组与管理员菜单分组平级（parent_id=0），不挂在 menu:group 下
-- ============================================

-- 1. 患者菜单权限分组（type=1 菜单，顶级 parent_id=0，与管理员菜单分组平级）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES (
    'patient-menu:group',
    '患者菜单权限分组',
    1,
    0,
    2,
    0
);

-- 2. 患者端菜单项（type=1，挂在 patient-menu:group 下）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'patient:home:menu',
    '患者首页',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'patient:consultation:menu',
    '在线问诊',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'patient:diagnosis:menu',
    '皮肤诊断',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    2,
    0
),
(
    'patient:knowledge:menu',
    '病知识库',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    3,
    0
);

-- 3. 医生菜单权限分组（type=1 菜单，顶级 parent_id=0，与管理员菜单分组平级）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES (
    'doctor-menu:group',
    '医生菜单权限分组',
    1,
    0,
    3,
    0
);

-- 4. 医生端菜单项（type=1，挂在 doctor-menu:group 下）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'doctor:home:menu',
    '工作台',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'doctor:consultation:chat:menu',
    '在线问诊',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'doctor:consultation:menu',
    '问诊管理',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    2,
    0
),
(
    'doctor:patients:menu',
    '患者管理',
    1,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor-menu:group' AND `is_deleted` = 0 LIMIT 1) t),
    3,
    0
);

-- 5. 患者端按钮权限（type=2，挂在对应菜单下）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'patient:consultation:create',
    '发起问诊',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient:consultation:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'patient:diagnosis:upload',
    '上传诊断',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'patient:diagnosis:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
);

-- 6. 医生端按钮权限（type=2，挂在对应菜单下）
INSERT IGNORE INTO `sys_permission` (
    `permission_code`,
    `permission_name`,
    `permission_type`,
    `parent_id`,
    `sort`,
    `is_deleted`
) VALUES
(
    'doctor:consultation:start',
    '开始问诊',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor:consultation:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
),
(
    'doctor:consultation:end',
    '结束问诊',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor:consultation:menu' AND `is_deleted` = 0 LIMIT 1) t),
    1,
    0
),
(
    'doctor:patient:view',
    '查看患者',
    2,
    (SELECT id FROM (SELECT id FROM `sys_permission` WHERE `permission_code` = 'doctor:patients:menu' AND `is_deleted` = 0 LIMIT 1) t),
    0,
    0
);

-- 7. 为患者角色关联菜单 + 路由 + 按钮权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN `sys_permission` p
WHERE r.role_code = 'patient' AND r.is_deleted = 0
  AND p.is_deleted = 0
  AND p.permission_code IN (
    'patient-menu:group',
    'patient:home:menu',
    'patient:consultation:menu',
    'patient:diagnosis:menu',
    'patient:home',
    'patient:consultation',
    'patient:diagnosis',
    'patient:consultation:create',
    'patient:diagnosis:upload'
  );

-- 8. 为医生角色关联菜单 + 路由 + 按钮权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN `sys_permission` p
WHERE r.role_code = 'doctor' AND r.is_deleted = 0
  AND p.is_deleted = 0
  AND p.permission_code IN (
    'doctor-menu:group',
    'doctor:home:menu',
    'doctor:consultation:chat:menu',
    'doctor:consultation:menu',
    'doctor:patients:menu',
    'doctor:home',
    'doctor:consultation',
    'doctor:patients',
    'doctor:consultation:start',
    'doctor:consultation:end',
    'doctor:patient:view'
  );
