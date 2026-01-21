package com.ccs.ipc.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 密码加密工具类
 *
 * @author WZH
 * @since 2026-01-19
 */
public class PasswordUtil {

    /**
     * 使用MD5加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return DigestUtils.md5Hex(password);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        String encoded = encode(rawPassword);
        return encoded.equals(encodedPassword);
    }
}

