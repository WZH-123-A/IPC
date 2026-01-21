package com.ccs.ipc;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccs.ipc.common.util.PasswordUtil;
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
    public static final byte DELETED = 1;
    public static final byte NOT_DELETED = 0;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Test
    public void test0(){
        SysUser sysUser = new SysUser();
        sysUser.setUsername("admin1");
        String passWord = "123456";
        String md5Password = PasswordUtil.encode(passWord);
        sysUser.setPassword(md5Password);
        sysUser.setPhone("15623113922");
        sysUser.setEmail("3134925395@qq.com");
        sysUser.setRealName("伍智豪");
        sysUser.setGender(MALE);
        sysUser.setStatus(ENABLE);
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

    @Test
    public void test3(){
        // 查询userId为1的用户
        SysUser sysUser = sysUserMapper.selectById(1L);
        if (sysUser != null) {
            // 如果用户密码还未加密（可能是明文），则进行MD5加密
            String plainPassword = sysUser.getPassword();
            String md5Password = PasswordUtil.encode(plainPassword);
            
            // 更新密码为MD5加密后的密码
            sysUser.setPassword(md5Password);
            sysUserMapper.updateById(sysUser);
            
            System.out.println("用户ID: " + sysUser.getId());
            System.out.println("用户名: " + sysUser.getUsername());
            System.out.println("原始密码: " + plainPassword);
            System.out.println("MD5加密后密码: " + md5Password);
            System.out.println("密码更新成功！");
        } else {
            System.out.println("用户ID为1的用户不存在！");
        }
    }
}
