-- ============================================
-- 更新权限排序：每一组的子权限排序都从0开始
-- ============================================

-- 菜单权限分组 (parent_id=248) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 1 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 2 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 3 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 4 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 5 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 5 WHERE `id` = 6 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 6 WHERE `id` = 50 AND `parent_id` = 248;
UPDATE `sys_permission` SET `sort` = 7 WHERE `id` = 62 AND `parent_id` = 248;

-- 系统管理 (parent_id=1) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 7 AND `parent_id` = 1;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 8 AND `parent_id` = 1;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 9 AND `parent_id` = 1;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 47 AND `parent_id` = 1;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 48 AND `parent_id` = 1;
UPDATE `sys_permission` SET `sort` = 5 WHERE `id` = 49 AND `parent_id` = 1;

-- 患者管理 (parent_id=2) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 10 AND `parent_id` = 2;

-- 医生管理 (parent_id=3) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 11 AND `parent_id` = 3;

-- 问诊管理 (parent_id=4) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 12 AND `parent_id` = 4;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 53 AND `parent_id` = 4;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 54 AND `parent_id` = 4;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 55 AND `parent_id` = 4;

-- 诊断管理 (parent_id=5) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 13 AND `parent_id` = 5;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 56 AND `parent_id` = 5;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 57 AND `parent_id` = 5;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 58 AND `parent_id` = 5;

-- 知识库管理 (parent_id=6) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 14 AND `parent_id` = 6;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 59 AND `parent_id` = 6;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 60 AND `parent_id` = 6;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 61 AND `parent_id` = 6;

-- 用户信息管理 (parent_id=50) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 51 AND `parent_id` = 50;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 52 AND `parent_id` = 50;

-- 统计分析 (parent_id=62) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 63 AND `parent_id` = 62;

-- 用户管理 (parent_id=7) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 15 AND `parent_id` = 7;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 16 AND `parent_id` = 7;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 17 AND `parent_id` = 7;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 18 AND `parent_id` = 7;

-- 角色管理 (parent_id=8) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 19 AND `parent_id` = 8;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 20 AND `parent_id` = 8;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 21 AND `parent_id` = 8;

-- 患者列表 (parent_id=10) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 22 AND `parent_id` = 10;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 23 AND `parent_id` = 10;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 24 AND `parent_id` = 10;

-- 文件管理 (parent_id=47) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 64 AND `parent_id` = 47;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 65 AND `parent_id` = 47;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 66 AND `parent_id` = 47;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 67 AND `parent_id` = 47;

-- 访问日志 (parent_id=48) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 68 AND `parent_id` = 48;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 69 AND `parent_id` = 48;

-- 操作日志 (parent_id=49) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 70 AND `parent_id` = 49;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 71 AND `parent_id` = 49;

-- 患者信息 (parent_id=51) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 72 AND `parent_id` = 51;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 73 AND `parent_id` = 51;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 74 AND `parent_id` = 51;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 75 AND `parent_id` = 51;

-- 医生信息 (parent_id=52) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 76 AND `parent_id` = 52;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 77 AND `parent_id` = 52;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 78 AND `parent_id` = 52;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 79 AND `parent_id` = 52;

-- 问诊会话 (parent_id=53) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 80 AND `parent_id` = 53;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 81 AND `parent_id` = 53;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 82 AND `parent_id` = 53;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 83 AND `parent_id` = 53;

-- 问诊消息 (parent_id=54) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 84 AND `parent_id` = 54;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 85 AND `parent_id` = 54;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 86 AND `parent_id` = 54;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 87 AND `parent_id` = 54;

-- 问诊评价 (parent_id=55) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 88 AND `parent_id` = 55;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 89 AND `parent_id` = 55;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 90 AND `parent_id` = 55;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 91 AND `parent_id` = 55;

-- 皮肤诊断记录 (parent_id=56) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 92 AND `parent_id` = 56;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 93 AND `parent_id` = 56;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 94 AND `parent_id` = 56;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 95 AND `parent_id` = 56;

-- 诊断结果 (parent_id=57) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 96 AND `parent_id` = 57;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 97 AND `parent_id` = 57;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 98 AND `parent_id` = 57;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 99 AND `parent_id` = 57;

-- 疾病类型 (parent_id=58) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 100 AND `parent_id` = 58;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 101 AND `parent_id` = 58;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 102 AND `parent_id` = 58;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 103 AND `parent_id` = 58;

-- 知识库分类 (parent_id=59) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 104 AND `parent_id` = 59;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 105 AND `parent_id` = 59;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 106 AND `parent_id` = 59;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 107 AND `parent_id` = 59;

-- 知识库内容 (parent_id=60) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 108 AND `parent_id` = 60;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 109 AND `parent_id` = 60;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 110 AND `parent_id` = 60;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 111 AND `parent_id` = 60;

-- 知识库标签 (parent_id=61) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 112 AND `parent_id` = 61;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 113 AND `parent_id` = 61;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 114 AND `parent_id` = 61;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 115 AND `parent_id` = 61;

-- 每日统计 (parent_id=63) 的按钮权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 116 AND `parent_id` = 63;

-- 接口权限分组 (parent_id=250) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 224 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 225 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 226 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 227 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 228 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 5 WHERE `id` = 229 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 6 WHERE `id` = 230 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 7 WHERE `id` = 231 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 8 WHERE `id` = 232 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 9 WHERE `id` = 233 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 10 WHERE `id` = 234 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 11 WHERE `id` = 235 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 12 WHERE `id` = 236 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 13 WHERE `id` = 237 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 14 WHERE `id` = 238 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 15 WHERE `id` = 239 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 16 WHERE `id` = 240 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 17 WHERE `id` = 241 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 18 WHERE `id` = 242 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 19 WHERE `id` = 243 AND `parent_id` = 250;
UPDATE `sys_permission` SET `sort` = 20 WHERE `id` = 212 AND `parent_id` = 250;

-- 用户管理接口分组 (parent_id=224) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 25 AND `parent_id` = 224;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 26 AND `parent_id` = 224;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 27 AND `parent_id` = 224;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 28 AND `parent_id` = 224;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 29 AND `parent_id` = 224;

-- 角色管理接口分组 (parent_id=225) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 30 AND `parent_id` = 225;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 31 AND `parent_id` = 225;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 32 AND `parent_id` = 225;

-- 患者接口分组 (parent_id=243) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 33 AND `parent_id` = 243;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 34 AND `parent_id` = 243;

-- 问诊接口分组 (parent_id=234) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 35 AND `parent_id` = 234;

-- 诊断接口分组 (parent_id=238) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 36 AND `parent_id` = 238;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 37 AND `parent_id` = 238;

-- 文件管理接口分组 (parent_id=226) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 117 AND `parent_id` = 226;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 118 AND `parent_id` = 226;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 119 AND `parent_id` = 226;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 120 AND `parent_id` = 226;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 121 AND `parent_id` = 226;

-- 访问日志接口分组 (parent_id=227) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 122 AND `parent_id` = 227;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 123 AND `parent_id` = 227;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 124 AND `parent_id` = 227;

-- 操作日志接口分组 (parent_id=228) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 125 AND `parent_id` = 228;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 126 AND `parent_id` = 228;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 127 AND `parent_id` = 228;

-- 患者信息接口分组 (parent_id=229) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 128 AND `parent_id` = 229;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 129 AND `parent_id` = 229;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 130 AND `parent_id` = 229;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 131 AND `parent_id` = 229;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 132 AND `parent_id` = 229;

-- 医生信息接口分组 (parent_id=230) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 133 AND `parent_id` = 230;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 134 AND `parent_id` = 230;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 135 AND `parent_id` = 230;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 136 AND `parent_id` = 230;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 137 AND `parent_id` = 230;

-- 问诊会话接口分组 (parent_id=231) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 138 AND `parent_id` = 231;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 139 AND `parent_id` = 231;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 140 AND `parent_id` = 231;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 141 AND `parent_id` = 231;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 142 AND `parent_id` = 231;

-- 问诊消息接口分组 (parent_id=232) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 143 AND `parent_id` = 232;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 144 AND `parent_id` = 232;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 145 AND `parent_id` = 232;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 146 AND `parent_id` = 232;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 147 AND `parent_id` = 232;

-- 问诊评价接口分组 (parent_id=233) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 148 AND `parent_id` = 233;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 149 AND `parent_id` = 233;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 150 AND `parent_id` = 233;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 151 AND `parent_id` = 233;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 152 AND `parent_id` = 233;

-- 皮肤诊断记录接口分组 (parent_id=235) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 153 AND `parent_id` = 235;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 154 AND `parent_id` = 235;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 155 AND `parent_id` = 235;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 156 AND `parent_id` = 235;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 157 AND `parent_id` = 235;

-- 诊断结果接口分组 (parent_id=236) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 158 AND `parent_id` = 236;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 159 AND `parent_id` = 236;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 160 AND `parent_id` = 236;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 161 AND `parent_id` = 236;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 162 AND `parent_id` = 236;

-- 疾病类型接口分组 (parent_id=237) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 163 AND `parent_id` = 237;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 164 AND `parent_id` = 237;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 165 AND `parent_id` = 237;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 166 AND `parent_id` = 237;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 167 AND `parent_id` = 237;

-- 知识库分类接口分组 (parent_id=239) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 168 AND `parent_id` = 239;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 169 AND `parent_id` = 239;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 170 AND `parent_id` = 239;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 171 AND `parent_id` = 239;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 172 AND `parent_id` = 239;

-- 知识库内容接口分组 (parent_id=240) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 173 AND `parent_id` = 240;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 174 AND `parent_id` = 240;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 175 AND `parent_id` = 240;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 176 AND `parent_id` = 240;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 177 AND `parent_id` = 240;

-- 知识库标签接口分组 (parent_id=241) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 178 AND `parent_id` = 241;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 179 AND `parent_id` = 241;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 180 AND `parent_id` = 241;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 181 AND `parent_id` = 241;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 182 AND `parent_id` = 241;

-- 每日统计接口分组 (parent_id=242) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 183 AND `parent_id` = 242;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 184 AND `parent_id` = 242;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 185 AND `parent_id` = 242;

-- 权限管理接口分组 (parent_id=212) 的接口权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 202 AND `parent_id` = 212;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 203 AND `parent_id` = 212;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 204 AND `parent_id` = 212;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 205 AND `parent_id` = 212;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 206 AND `parent_id` = 212;

-- 路由权限分组 (parent_id=251) 的子权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 244 AND `parent_id` = 251;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 245 AND `parent_id` = 251;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 246 AND `parent_id` = 251;

-- 患者路由分组 (parent_id=244) 的路由权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 38 AND `parent_id` = 244;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 39 AND `parent_id` = 244;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 40 AND `parent_id` = 244;

-- 医生路由分组 (parent_id=245) 的路由权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 41 AND `parent_id` = 245;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 42 AND `parent_id` = 245;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 43 AND `parent_id` = 245;

-- 管理员路由分组 (parent_id=246) 的路由权限
UPDATE `sys_permission` SET `sort` = 0 WHERE `id` = 44 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 1 WHERE `id` = 45 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 2 WHERE `id` = 46 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 3 WHERE `id` = 186 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 4 WHERE `id` = 187 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 5 WHERE `id` = 188 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 6 WHERE `id` = 189 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 7 WHERE `id` = 190 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 8 WHERE `id` = 191 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 9 WHERE `id` = 192 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 10 WHERE `id` = 193 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 11 WHERE `id` = 194 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 12 WHERE `id` = 195 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 13 WHERE `id` = 196 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 14 WHERE `id` = 197 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 15 WHERE `id` = 198 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 16 WHERE `id` = 199 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 17 WHERE `id` = 200 AND `parent_id` = 246;
UPDATE `sys_permission` SET `sort` = 18 WHERE `id` = 201 AND `parent_id` = 246;

