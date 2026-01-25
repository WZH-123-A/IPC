# 权限控制使用说明

## 后端接口

### 1. 获取当前用户的菜单权限树
```
GET /api/auth/menus
响应: {
  code: 200,
  data: [
    {
      id: 1,
      permissionCode: "system:menu",
      permissionName: "系统管理",
      permissionType: 1,
      parentId: 0,
      sort: 0,
      children: [
        {
          id: 7,
          permissionCode: "system:user:menu",
          permissionName: "用户管理",
          permissionType: 1,
          parentId: 1,
          sort: 0
        }
      ]
    }
  ]
}
```

### 2. 获取当前用户的按钮权限列表
```
GET /api/auth/buttons
响应: {
  code: 200,
  data: [
    "system:user:add",
    "system:user:edit",
    "system:user:delete",
    "system:user:query"
  ]
}
```

## 前端使用

### 1. 菜单权限控制

菜单已经通过 `AdminSidebar` 组件自动根据权限动态生成，无需手动控制。

### 2. 按钮权限控制

#### 方式一：使用 v-permission 指令
```vue
<template>
  <el-button v-permission="'system:user:add'" type="primary" @click="handleAdd">
    新增用户
  </el-button>
  
  <el-button v-permission="'system:user:edit'" type="primary" @click="handleEdit">
    编辑
  </el-button>
  
  <el-button v-permission="'system:user:delete'" type="danger" @click="handleDelete">
    删除
  </el-button>
</template>
```

#### 方式二：使用 hasPermission 函数
```vue
<template>
  <el-button v-if="hasPermission('system:user:add')" type="primary" @click="handleAdd">
    新增用户
  </el-button>
</template>

<script setup lang="ts">
import { hasPermission } from '@/directives/permission'
</script>
```

### 3. 在 BaseManageView 中使用

修改 `BaseManageView.vue` 组件，添加权限控制：

```vue
<template>
  <el-button 
    v-permission="addPermission" 
    type="primary" 
    @click="handleAdd"
  >
    新增
  </el-button>
  
  <el-button 
    v-permission="editPermission" 
    type="primary" 
    link 
    size="small" 
    @click="handleEdit(row)"
  >
    编辑
  </el-button>
  
  <el-button 
    v-permission="deletePermission" 
    type="danger" 
    link 
    size="small" 
    @click="handleDelete(row)"
  >
    删除
  </el-button>
</template>

<script setup lang="ts">
interface Props {
  addPermission?: string
  editPermission?: string
  deletePermission?: string
}
</script>
```

## 权限编码规范

- 菜单权限：`模块:功能:menu`，如 `system:user:menu`
- 按钮权限：`模块:功能:操作`，如 `system:user:add`、`system:user:edit`、`system:user:delete`
- 路由权限：`角色:页面`，如 `admin:home`、`patient:home`

## 注意事项

1. 用户登录后，权限信息会保存在 `localStorage` 或 `sessionStorage` 的 `userInfo` 中
2. 菜单权限会自动从后端获取并动态生成
3. 按钮权限需要在前端页面中手动添加 `v-permission` 指令或使用 `hasPermission` 函数
4. 路由权限已经在路由守卫中处理，无需额外配置

