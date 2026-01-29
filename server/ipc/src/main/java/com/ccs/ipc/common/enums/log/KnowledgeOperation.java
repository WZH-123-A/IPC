package com.ccs.ipc.common.enums.log;

import lombok.Getter;

/**
 * 知识库管理模块（KnowledgeController）操作方法
 */
@Getter
public enum KnowledgeOperation implements IOperationType {
    UPLOAD_IMAGE("知识库图片上传"),
    CATEGORY_LIST("分页查询知识库分类列表"),
    CATEGORY_DETAIL("根据ID获取知识库分类详情"),
    CATEGORY_ADD("新增知识库分类"),
    CATEGORY_UPDATE("更新知识库分类"),
    CATEGORY_DELETE("删除知识库分类"),
    CONTENT_LIST("分页查询知识库内容列表"),
    CONTENT_DETAIL("根据ID获取知识库内容详情"),
    CONTENT_ADD("新增知识库内容"),
    CONTENT_UPDATE("更新知识库内容"),
    CONTENT_DELETE("删除知识库内容"),
    TAG_LIST("分页查询知识库标签列表"),
    TAG_DETAIL("根据ID获取知识库标签详情"),
    TAG_ADD("新增知识库标签"),
    TAG_UPDATE("更新知识库标签"),
    TAG_DELETE("删除知识库标签");

    /** 供 @Log 注解使用的编译期常量 */
    public static final class C {
        public static final String UPLOAD_IMAGE = "知识库图片上传";
        public static final String CATEGORY_LIST = "分页查询知识库分类列表";
        public static final String CATEGORY_DETAIL = "根据ID获取知识库分类详情";
        public static final String CATEGORY_ADD = "新增知识库分类";
        public static final String CATEGORY_UPDATE = "更新知识库分类";
        public static final String CATEGORY_DELETE = "删除知识库分类";
        public static final String CONTENT_LIST = "分页查询知识库内容列表";
        public static final String CONTENT_DETAIL = "根据ID获取知识库内容详情";
        public static final String CONTENT_ADD = "新增知识库内容";
        public static final String CONTENT_UPDATE = "更新知识库内容";
        public static final String CONTENT_DELETE = "删除知识库内容";
        public static final String TAG_LIST = "分页查询知识库标签列表";
        public static final String TAG_DETAIL = "根据ID获取知识库标签详情";
        public static final String TAG_ADD = "新增知识库标签";
        public static final String TAG_UPDATE = "更新知识库标签";
        public static final String TAG_DELETE = "删除知识库标签";
    }

    private final String value;

    KnowledgeOperation(String value) {
        this.value = value;
    }
}
