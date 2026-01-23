-- ============================================
-- 管理员角色权限关联数据插入
-- 先删除表，然后重新创建表，最后插入管理员角色的所有权限关联
-- ============================================

-- 1. 删除 sys_role_permission 表
DROP TABLE IF EXISTS `sys_role_permission`;

-- 2. 重新创建 sys_role_permission 表
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 3. 将 sys_permission 表中所有未删除的权限关联到管理员角色（role_id = 1）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT
    1 AS role_id,
    p.id AS permission_id
FROM `sys_permission` p
WHERE p.is_deleted = 0;

-- 4. 验证：查询管理员角色关联的权限数量
-- SELECT COUNT(*) as permission_count FROM sys_role_permission WHERE role_id = 1;

