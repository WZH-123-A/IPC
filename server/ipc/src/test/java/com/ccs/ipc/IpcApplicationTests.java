package com.ccs.ipc;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccs.ipc.entity.SysUser;
import com.ccs.ipc.entity.SysUserRole;
import com.ccs.ipc.mapper.SysUserMapper;
import com.ccs.ipc.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IpcApplicationTests {
    public static final byte MALE = 1;
    public static final byte FEMALE = 2;
    public static final byte DISABLE = 0;
    public static final byte ENABLE = 1;
    public static final byte DELETED = 0;
    public static final byte NOT_DELETED = 1;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Test
    public void test0(){
        SysUser sysUser = new SysUser();
        sysUser.setUsername("admin1");
        sysUser.setPassword("123456");
        sysUser.setPhone("15623113922");
        sysUser.setEmail("3134925395@qq.com");
        sysUser.setRealName("伍智豪");
        sysUser.setGender(MALE);
        sysUser.setStatus(ENABLE);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUser.setIsDeleted(NOT_DELETED);
        sysUserMapper.insert(sysUser);
    }

    @Test
    public void test1(){
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(1L);
        sysUserRole.setRoleId(1L);
        sysUserRole.setCreateTime(LocalDateTime.now());
        sysUserRoleMapper.insert(sysUserRole);
    }
    @Test
    public void test2(){
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username","admin1");
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        System.out.println(sysUser);
    }

}
