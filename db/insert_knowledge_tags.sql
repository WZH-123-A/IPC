-- ============================================
-- 知识库标签初始数据及与内容的关联
-- 执行前请确保已执行 insert_disease_knowledge.sql（存在 knowledge_category、knowledge_content 数据）
-- 使用 INSERT IGNORE 避免标签名重复
-- ============================================

-- 1. 知识库标签（皮肤病/护理相关，带颜色）
INSERT IGNORE INTO `knowledge_tag` (`tag_name`, `tag_color`, `use_count`, `is_deleted`) VALUES
('湿疹', '#E6A23C', 0, 0),
('皮炎', '#F56C6C', 0, 0),
('真菌感染', '#409EFF', 0, 0),
('日常护理', '#67C23A', 0, 0),
('保湿', '#909399', 0, 0),
('防晒', '#E6A23C', 0, 0),
('皮肤结构', '#409EFF', 0, 0),
('预防', '#67C23A', 0, 0),
('用药', '#F56C6C', 0, 0),
('足癣', '#909399', 0, 0),
('体癣', '#409EFF', 0, 0),
('股癣', '#409EFF', 0, 0),
('接触性皮炎', '#F56C6C', 0, 0),
('神经性皮炎', '#E6A23C', 0, 0),
('护肤', '#67C23A', 0, 0),
('识别', '#909399', 0, 0),
('过敏原', '#F56C6C', 0, 0),
('止痒', '#E6A23C', 0, 0);

-- 2. 内容-标签关联（按内容标题匹配，与 insert_disease_knowledge 中的标题一致）
INSERT IGNORE INTO `knowledge_content_tag` (`content_id`, `tag_id`)
SELECT c.id, t.id
FROM `knowledge_content` c
CROSS JOIN `knowledge_tag` t
WHERE c.is_deleted = 0 AND t.is_deleted = 0
  AND (
    (c.title = '皮肤的结构与功能' AND t.tag_name IN ('皮肤结构', '日常护理', '预防'))
    OR (c.title = '如何初步判断皮肤病类型' AND t.tag_name IN ('识别', '预防'))
    OR (c.title = '湿疹的常见原因与诱因' AND t.tag_name IN ('湿疹', '预防'))
    OR (c.title = '湿疹患者日常护理要点' AND t.tag_name IN ('湿疹', '日常护理', '保湿'))
    OR (c.title = '接触性皮炎：识别与预防' AND t.tag_name IN ('接触性皮炎', '皮炎', '过敏原', '预防'))
    OR (c.title = '神经性皮炎与生活习惯' AND t.tag_name IN ('神经性皮炎', '皮炎', '止痒', '日常护理'))
    OR (c.title = '足癣（脚气）的防治' AND t.tag_name IN ('足癣', '真菌感染', '用药', '预防'))
    OR (c.title = '体癣与股癣的识别与用药' AND t.tag_name IN ('体癣', '股癣', '真菌感染', '用药', '预防'))
    OR (c.title = '秋冬季节皮肤保湿指南' AND t.tag_name IN ('保湿', '日常护理', '护肤'))
    OR (c.title = '防晒与皮肤健康' AND t.tag_name IN ('防晒', '护肤', '预防'))
  );

-- 3. 更新标签使用次数
UPDATE `knowledge_tag` t
SET t.use_count = (
  SELECT COUNT(*) FROM `knowledge_content_tag` ct
  WHERE ct.tag_id = t.id
)
WHERE t.is_deleted = 0;
