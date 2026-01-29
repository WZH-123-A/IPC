-- ============================================
-- 疾病知识库初始数据
-- 执行前请确保 knowledge_category、knowledge_content 表已存在
-- 使用 INSERT IGNORE 避免分类编码重复
-- ============================================

-- 1. 知识库分类（皮肤病相关）
INSERT IGNORE INTO `knowledge_category` (`category_code`, `category_name`, `parent_id`, `sort`, `icon`, `description`, `is_deleted`) VALUES
('skin_basics', '皮肤病常识', 0, 0, '', '皮肤结构与常见皮肤病基础知识', 0),
('eczema', '湿疹', 0, 1, '', '湿疹的病因、症状与防治', 0),
('dermatitis', '皮炎', 0, 2, '', '各类皮炎的识别与护理', 0),
('fungal', '真菌感染', 0, 3, '', '癣类等真菌性皮肤病知识', 0),
('daily_care', '日常护理', 0, 4, '', '皮肤日常护理与预防', 0);

-- 2. 知识库内容（图文类型=1，已发布=1）
-- 皮肤病常识
INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '皮肤的结构与功能', '了解人体最大器官', 1,
'<p>皮肤是人体最大的器官，总表面积约1.5-2平方米。皮肤由表皮、真皮和皮下组织三层构成。</p>
<h3>表皮</h3><p>最外层，主要起屏障保护作用，含有角质层、透明层、颗粒层等。表皮细胞不断更新，约28天完成一次代谢周期。</p>
<h3>真皮</h3><p>位于表皮下方，含有胶原纤维、弹力纤维，以及血管、神经、毛囊、汗腺等。真皮赋予皮肤弹性和韧性。</p>
<h3>皮下组织</h3><p>主要由脂肪组织构成，具有保温、缓冲、储存能量等功能。</p>
<p>健康的皮肤能够抵御外界刺激、调节体温、感受外界刺激，并参与免疫反应。日常防晒、保湿、清洁是维护皮肤健康的基础。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'skin_basics' AND `is_deleted` = 0 LIMIT 1;

INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '如何初步判断皮肤病类型', '常见皮肤问题的识别要点', 1,
'<p>皮肤出现红疹、瘙痒、脱屑时，可先根据部位和形态做初步判断。</p>
<h3>对称分布、瘙痒明显</h3><p>多见于湿疹、特应性皮炎，好发于肘窝、腘窝、手腕等屈侧。</p>
<h3>环形或半环形、边缘清楚</h3><p>常见于体癣、股癣等真菌感染，边缘可有小丘疹或水疱。</p>
<h3>成片红斑、脱屑</h3><p>银屑病（牛皮癣）多表现为境界清楚的红斑，上覆银白色鳞屑。</p>
<h3>水疱成簇</h3><p>带状疱疹多沿神经分布，呈带状排列，伴疼痛。</p>
<p>以上仅为参考，确诊需到皮肤科就诊，必要时做真菌镜检、皮肤镜等检查。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'skin_basics' AND `is_deleted` = 0 LIMIT 1;

-- 湿疹
INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '湿疹的常见原因与诱因', '从生活习惯到环境因素', 1,
'<p>湿疹是一种常见的慢性、炎症性、瘙痒性皮肤病，病因复杂，常与以下因素有关。</p>
<h3>内因</h3><p>遗传易感性（如特应性体质）、皮肤屏障功能异常、免疫失调、精神紧张、劳累、失眠等。</p>
<h3>外因</h3><p>接触过敏原或刺激物（如洗涤剂、化妆品、金属、羊毛）、气候干燥或潮湿、出汗、搔抓、感染等。</p>
<h3>常见诱因</h3><p>季节交替、熬夜、辛辣饮食、饮酒、热水烫洗、过度清洁等都可能加重或诱发湿疹。</p>
<p>患者应尽量避开已知诱因，做好保湿，避免搔抓，并在医生指导下规范用药。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'eczema' AND `is_deleted` = 0 LIMIT 1;

INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '湿疹患者日常护理要点', '保湿与避免刺激是关键', 1,
'<p>湿疹容易反复，日常护理与药物治疗同样重要。</p>
<h3>保湿</h3><p>每天多次涂抹保湿霜，尤其在洗澡后3分钟内，锁住水分。选择无香精、低敏的保湿产品。</p>
<h3>洗澡</h3><p>水温不宜过高（约37℃），时间控制在10分钟内，少用或不用碱性强的肥皂，洗后轻轻拍干。</p>
<h3>穿着</h3><p>穿宽松、透气的棉质衣物，避免化纤、羊毛直接接触皮肤。</p>
<h3>环境</h3><p>保持室内适宜湿度（40%-60%），避免过热、出汗过多。</p>
<h3>饮食与作息</h3><p>避免已知过敏食物，少食辛辣刺激；保证睡眠，放松情绪，有助于减少复发。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'eczema' AND `is_deleted` = 0 LIMIT 1;

-- 皮炎
INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '接触性皮炎：识别与预防', '远离过敏原和刺激物', 1,
'<p>接触性皮炎是皮肤接触外界物质后发生的炎症反应，分为刺激性接触性皮炎和变应性（过敏性）接触性皮炎。</p>
<h3>常见致敏/刺激物</h3><p>金属（镍、铬）、染发剂、化妆品、洗涤剂、橡胶、药物外用制剂、植物（如漆树）等。</p>
<h3>表现</h3><p>接触部位出现红斑、丘疹、水疱，严重时糜烂、渗出，伴瘙痒或灼痛，边界常与接触物形状一致。</p>
<h3>处理</h3><p>立即脱离可疑接触物，用清水冲洗；避免搔抓；在医生指导下外用或口服抗炎、抗过敏药物。</p>
<h3>预防</h3><p>明确过敏原后避免再次接触，必要时做斑贴试验协助诊断。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'dermatitis' AND `is_deleted` = 0 LIMIT 1;

INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '神经性皮炎与生活习惯', '减少搔抓与精神紧张', 1,
'<p>神经性皮炎又称慢性单纯性苔藓，以阵发性剧烈瘙痒和皮肤苔藓样变为特点，好发于颈侧、肘部、腰骶、眼睑等部位。</p>
<h3>病因</h3><p>与精神紧张、焦虑、失眠、局部摩擦刺激、搔抓等有关。搔抓会加重皮损，形成“越抓越痒、越痒越抓”的恶性循环。</p>
<h3>治疗原则</h3><p>止痒、抗炎、打破搔抓循环。医生常给予外用糖皮质激素或钙调神经磷酸酶抑制剂，必要时口服抗组胺药。患者需配合减少搔抓。</p>
<h3>生活调理</h3><p>规律作息、放松情绪、避免熬夜；穿柔软衣物减少摩擦；剪短指甲，痒时轻拍或冷敷代替搔抓。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'dermatitis' AND `is_deleted` = 0 LIMIT 1;

-- 真菌感染
INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '足癣（脚气）的防治', '保持足部干燥与规范用药', 1,
'<p>足癣是由皮肤癣菌感染足部皮肤引起的常见病，俗称“脚气”，多见于趾间、足底，表现为脱屑、水疱、糜烂、皲裂等。</p>
<h3>传播与易感因素</h3><p>共用拖鞋、浴盆、毛巾，或在公共浴室、泳池赤足行走易感染。足部多汗、穿不透气鞋袜、免疫力低下者更易发病。</p>
<h3>治疗</h3><p>以外用抗真菌药为主（如咪康唑、特比萘芬等），需足量、足疗程使用，一般连续2-4周。范围大或反复发作者可加用口服抗真菌药，需在医生指导下使用。</p>
<h3>预防</h3><p>保持足部清洁干燥，穿透气鞋袜；不与他人共用鞋袜、脚盆；公共场所尽量不赤足。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'fungal' AND `is_deleted` = 0 LIMIT 1;

INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '体癣与股癣的识别与用药', '避免误用激素', 1,
'<p>体癣指发生在除手足、头皮、甲板以外光滑皮肤上的癣；股癣指发生在腹股沟、会阴、臀部的癣。二者病原菌相似，均为皮肤癣菌。</p>
<h3>典型表现</h3><p>环形或半环形红斑，边缘隆起、可有丘疹或小水疱，中央趋于消退，伴瘙痒。股癣因摩擦、潮湿，边界有时不如体癣清晰。</p>
<h3>注意</h3><p>不要误用激素药膏，否则可能加重或扩散（癣菌疹）。应使用抗真菌外用药，必要时口服抗真菌药。</p>
<h3>预防</h3><p>避免接触患癣的人或动物；自身有足癣、甲癣者需同时治疗，防止自身接种；保持局部干燥、透气。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'fungal' AND `is_deleted` = 0 LIMIT 1;

-- 日常护理
INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '秋冬季节皮肤保湿指南', '预防干燥与瘙痒', 1,
'<p>秋冬季节气温下降、湿度降低，皮肤容易干燥、脱屑、瘙痒，甚至诱发或加重湿疹。科学保湿可有效缓解。</p>
<h3>清洁</h3><p>选用温和、弱酸性洁面/沐浴产品，避免过热的水和过度搓洗，洗澡频率可适当降低。</p>
<h3>保湿</h3><p>洗澡后趁皮肤微潮立即涂抹身体乳或保湿霜，锁住水分。面部可选用适合自己肤质的保湿乳液或面霜，干性皮肤可选用更滋润的霜剂。</p>
<h3>环境</h3><p>室内使用加湿器，将湿度维持在40%-60%，有助于减少经皮水分丢失。</p>
<h3>防晒</h3><p>秋冬紫外线仍存在，户外活动时建议继续使用防晒或保湿型防晒产品。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'daily_care' AND `is_deleted` = 0 LIMIT 1;

INSERT INTO `knowledge_content` (`category_id`, `title`, `subtitle`, `content_type`, `content`, `source`, `author`, `view_count`, `like_count`, `share_count`, `status`, `publish_time`, `is_deleted`)
SELECT id, '防晒与皮肤健康', '预防光老化和皮肤肿瘤', 1,
'<p>长期日晒可导致晒伤、色素沉着、光老化（皱纹、松弛、色斑），并增加皮肤癌风险。正确防晒是护肤的重要一环。</p>
<h3>防晒方式</h3><p>物理防晒：遮阳伞、帽子、长袖衣物、避开强紫外线时段（如10:00-16:00）。化学/物理防晒：涂抹防晒霜，SPF、PA 值根据场合选择，并注意足量、定时补涂。</p>
<h3>特殊人群</h3><p>儿童、孕妇、敏感肌宜选用物理防晒或配方温和的防晒产品。痤疮、玫瑰痤疮等患者在医生指导下选择不加重病情的防晒方式。</p>
<p>防晒应成为日常习惯，与保湿、清洁一起构成基础护肤三步。</p>',
'皮肤科科普', '系统', 0, 0, 0, 1, NOW(), 0
FROM `knowledge_category` WHERE `category_code` = 'daily_care' AND `is_deleted` = 0 LIMIT 1;
