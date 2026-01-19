-- ============================================
-- AI 智能问诊综合性医疗辅助平台 - 数据库设计
-- ============================================

-- ----------------------------
-- 1. 权限核心表 - 权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_code` varchar(64) NOT NULL COMMENT '权限编码（如：patient:info:query）',
  `permission_name` varchar(64) NOT NULL COMMENT '权限名称（如：患者信息查询）',
  `permission_type` tinyint NOT NULL COMMENT '权限类型：1-菜单权限 2-按钮权限 3-接口权限',
  `parent_id` bigint DEFAULT 0 COMMENT '父权限ID（0为顶级）',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- ----------------------------
-- 2. 权限核心表 - 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(64) NOT NULL COMMENT '角色编码（如：patient/doctor/admin）',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称（患者/医生/管理员）',
  `role_desc` varchar(255) DEFAULT '' COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ----------------------------
-- 3. 权限核心表 - 角色-权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
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

-- ----------------------------
-- 4. 基础用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(128) NOT NULL COMMENT '密码（MD5/SHA256加密）',
  `phone` varchar(20) DEFAULT '' COMMENT '手机号',
  `email` varchar(64) DEFAULT '' COMMENT '邮箱',
  `real_name` varchar(32) DEFAULT '' COMMENT '真实姓名',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像URL',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（统一存储患者/医生/管理员）';

-- ----------------------------
-- 5. 用户-角色关联表（支持多角色）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ----------------------------
-- 6. 患者扩展信息表（用户表的补充，仅患者角色有数据）
-- ----------------------------
DROP TABLE IF EXISTS `patient_info`;
CREATE TABLE `patient_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联sys_user.id',
  `age` int DEFAULT 0 COMMENT '年龄',
  `address` varchar(255) DEFAULT '' COMMENT '住址',
  `medical_history` text COMMENT '既往病史',
  `allergy_history` text COMMENT '过敏史',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者扩展信息表';

-- ----------------------------
-- 7. 医生扩展信息表（用户表的补充，仅医生角色有数据）
-- ----------------------------
DROP TABLE IF EXISTS `doctor_info`;
CREATE TABLE `doctor_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联sys_user.id',
  `hospital` varchar(128) DEFAULT '' COMMENT '所属医院',
  `department` varchar(64) DEFAULT '' COMMENT '科室（默认皮肤科）',
  `title` varchar(64) DEFAULT '' COMMENT '职称（主任医师/副主任医师等）',
  `specialty` varchar(255) DEFAULT '' COMMENT '擅长领域',
  `license_no` varchar(64) DEFAULT '' COMMENT '医师资格证编号',
  `work_years` int DEFAULT 0 COMMENT '从业年限',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_license_no` (`license_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生扩展信息表';

-- ----------------------------
-- 8. 疾病类型表（皮肤病分类）
-- ----------------------------
DROP TABLE IF EXISTS `disease_type`;
CREATE TABLE `disease_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '疾病ID',
  `disease_code` varchar(64) NOT NULL COMMENT '疾病编码',
  `disease_name` varchar(128) NOT NULL COMMENT '疾病名称',
  `disease_category` varchar(64) DEFAULT '' COMMENT '疾病分类（如：湿疹、皮炎、真菌感染等）',
  `description` text COMMENT '疾病描述',
  `symptoms` text COMMENT '常见症状',
  `treatment` text COMMENT '治疗方法',
  `prevention` text COMMENT '预防措施',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_disease_code` (`disease_code`),
  KEY `idx_category` (`disease_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病类型表';

-- ----------------------------
-- 9. 皮肤病识别记录表
-- ----------------------------
DROP TABLE IF EXISTS `skin_diagnosis_record`;
CREATE TABLE `skin_diagnosis_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '识别记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（患者）',
  `image_url` varchar(512) NOT NULL COMMENT '上传的图片URL',
  `image_name` varchar(255) DEFAULT '' COMMENT '图片名称',
  `body_part` varchar(64) DEFAULT '' COMMENT '身体部位（如：面部、手臂、腿部等）',
  `model_version` varchar(32) DEFAULT 'ResNet50' COMMENT '使用的模型版本',
  `status` tinyint DEFAULT 0 COMMENT '识别状态：0-识别中 1-识别成功 2-识别失败',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='皮肤病识别记录表';

-- ----------------------------
-- 10. 识别结果详情表
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_result`;
CREATE TABLE `diagnosis_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `record_id` bigint NOT NULL COMMENT '识别记录ID',
  `disease_id` bigint NOT NULL COMMENT '疾病ID',
  `confidence` decimal(5,2) NOT NULL COMMENT '置信度（0-100）',
  `rank` int DEFAULT 1 COMMENT '排名（1为最可能）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_disease_id` (`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='识别结果详情表';

-- ----------------------------
-- 11. 在线问诊会话表
-- ----------------------------
DROP TABLE IF EXISTS `consultation_session`;
CREATE TABLE `consultation_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_no` varchar(64) NOT NULL COMMENT '会话编号',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID（NULL表示AI问诊）',
  `session_type` tinyint NOT NULL COMMENT '会话类型：1-AI问诊 2-医生问诊',
  `title` varchar(255) DEFAULT '' COMMENT '问诊标题/主诉',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-进行中 1-已结束 2-已取消',
  `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_no` (`session_no`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线问诊会话表';

-- ----------------------------
-- 12. 问诊消息记录表
-- ----------------------------
DROP TABLE IF EXISTS `consultation_message`;
CREATE TABLE `consultation_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID（用户ID或0表示AI）',
  `sender_type` tinyint NOT NULL COMMENT '发送者类型：1-患者 2-医生 3-AI',
  `message_type` tinyint NOT NULL COMMENT '消息类型：1-文本 2-图片 3-语音 4-视频',
  `content` text COMMENT '消息内容（文本或文件URL）',
  `ai_model` varchar(64) DEFAULT '' COMMENT 'AI模型名称（如：讯飞星火）',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊消息记录表';

-- ----------------------------
-- 13. 问诊评价表
-- ----------------------------
DROP TABLE IF EXISTS `consultation_evaluation`;
CREATE TABLE `consultation_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint DEFAULT NULL COMMENT '医生ID（AI问诊时为NULL）',
  `rating` tinyint NOT NULL COMMENT '评分（1-5星）',
  `comment` text COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊评价表';

-- ----------------------------
-- 14. 知识库分类表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_category`;
CREATE TABLE `knowledge_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_code` varchar(64) NOT NULL COMMENT '分类编码',
  `category_name` varchar(128) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID（0为顶级）',
  `sort` int DEFAULT 0 COMMENT '排序',
  `icon` varchar(255) DEFAULT '' COMMENT '分类图标',
  `description` varchar(500) DEFAULT '' COMMENT '分类描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库分类表';

-- ----------------------------
-- 15. 知识库内容表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_content`;
CREATE TABLE `knowledge_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `subtitle` varchar(255) DEFAULT '' COMMENT '副标题',
  `content_type` tinyint NOT NULL COMMENT '内容类型：1-图文 2-视频 3-音频 4-文档',
  `cover_image` varchar(512) DEFAULT '' COMMENT '封面图片URL',
  `content` longtext COMMENT '正文内容（富文本）',
  `video_url` varchar(512) DEFAULT '' COMMENT '视频URL',
  `audio_url` varchar(512) DEFAULT '' COMMENT '音频URL',
  `document_url` varchar(512) DEFAULT '' COMMENT '文档URL',
  `source` varchar(128) DEFAULT '' COMMENT '来源（权威机构）',
  `author` varchar(64) DEFAULT '' COMMENT '作者',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `share_count` int DEFAULT 0 COMMENT '分享数',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下架',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库内容表';

-- ----------------------------
-- 16. 知识库标签表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_tag`;
CREATE TABLE `knowledge_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(64) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(16) DEFAULT '' COMMENT '标签颜色',
  `use_count` int DEFAULT 0 COMMENT '使用次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库标签表';

-- ----------------------------
-- 17. 知识库内容-标签关联表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_content_tag`;
CREATE TABLE `knowledge_content_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `content_id` bigint NOT NULL COMMENT '内容ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`,`tag_id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库内容-标签关联表';

-- ----------------------------
-- 18. 系统操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) DEFAULT '' COMMENT '用户名',
  `operation_type` varchar(32) NOT NULL COMMENT '操作类型（如：登录、查询、新增、修改、删除）',
  `operation_module` varchar(64) DEFAULT '' COMMENT '操作模块',
  `operation_desc` varchar(255) DEFAULT '' COMMENT '操作描述',
  `request_method` varchar(16) DEFAULT '' COMMENT '请求方法（GET/POST等）',
  `request_url` varchar(512) DEFAULT '' COMMENT '请求URL',
  `request_params` text COMMENT '请求参数',
  `response_data` text COMMENT '响应数据',
  `ip_address` varchar(64) DEFAULT '' COMMENT 'IP地址',
  `user_agent` varchar(512) DEFAULT '' COMMENT '用户代理',
  `status` tinyint DEFAULT 1 COMMENT '操作状态：0-失败 1-成功',
  `error_msg` text COMMENT '错误信息',
  `execution_time` bigint DEFAULT 0 COMMENT '执行时间（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- ----------------------------
-- 19. 数据统计表（用于数据驾驶舱）
-- ----------------------------
DROP TABLE IF EXISTS `statistics_daily`;
CREATE TABLE `statistics_daily` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` varchar(32) NOT NULL COMMENT '统计类型（user_register/consultation/diagnosis/knowledge_view等）',
  `stat_value` bigint DEFAULT 0 COMMENT '统计值',
  `extra_data` json DEFAULT NULL COMMENT '扩展数据（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type` (`stat_date`,`stat_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计数据表';

-- ----------------------------
-- 20. 文件上传记录表
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `file_path` varchar(512) NOT NULL COMMENT '文件路径',
  `file_url` varchar(512) NOT NULL COMMENT '文件访问URL',
  `file_type` varchar(32) DEFAULT '' COMMENT '文件类型（image/video/document等）',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小（字节）',
  `mime_type` varchar(128) DEFAULT '' COMMENT 'MIME类型',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `business_type` varchar(32) DEFAULT '' COMMENT '业务类型（diagnosis/consultation/knowledge等）',
  `business_id` bigint DEFAULT NULL COMMENT '业务ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_business` (`business_type`,`business_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录表';

-- ----------------------------
-- 21. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(128) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_desc` varchar(255) DEFAULT '' COMMENT '配置描述',
  `config_type` varchar(32) DEFAULT 'string' COMMENT '配置类型（string/number/boolean/json）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
