package com.ccs.ipc.common.util;

import com.ccs.ipc.common.annotation.Sensitive;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 敏感数据脱敏工具类
 *
 * @author WZH
 * @since 2026-01-19
 */
@Slf4j
public class SensitiveUtil {

    /**
     * 对对象进行脱敏处理（使用反射递归处理）
     *
     * @param obj 需要脱敏的对象
     * @return 脱敏后的对象（新对象，不修改原对象）
     */
    public static Object desensitize(Object obj) {
        if (obj == null) {
            return null;
        }

        // 如果是基本类型或String，直接返回
        if (isPrimitiveOrWrapper(obj.getClass()) || obj instanceof String) {
            return obj;
        }

        // 如果是集合类型
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            List<Object> result = new ArrayList<>();
            for (Object item : collection) {
                result.add(desensitize(item));
            }
            return result;
        }

        // 如果是Map类型
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey(), desensitize(entry.getValue()));
            }
            return result;
        }

        try {
            // 创建新对象
            Object newObj = obj.getClass().getDeclaredConstructor().newInstance();

            // 遍历所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段、final字段、serialVersionUID等特殊字段
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) 
                    || java.lang.reflect.Modifier.isFinal(field.getModifiers())
                    || "serialVersionUID".equals(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(obj);

                // 检查是否有@Sensitive注解
                Sensitive sensitive = field.getAnnotation(Sensitive.class);
                if (sensitive != null && value != null) {
                    // 对敏感字段进行脱敏
                    value = desensitizeValue(value, sensitive);
                } else if (value != null && !isPrimitiveOrWrapper(value.getClass()) && !(value instanceof String)) {
                    // 递归处理嵌套对象
                    value = desensitize(value);
                }

                field.set(newObj, value);
            }

            return newObj;
        } catch (Exception e) {
            log.warn("脱敏处理失败: {}", e.getMessage());
            return obj;
        }
    }

    /**
     * 对值进行脱敏处理
     *
     * @param value    原始值
     * @param sensitive 脱敏注解
     * @return 脱敏后的值
     */
    private static Object desensitizeValue(Object value, Sensitive sensitive) {
        if (value == null) {
            return null;
        }

        String strValue = String.valueOf(value);
        if (strValue.isEmpty()) {
            return strValue;
        }

        Sensitive.SensitiveType type = sensitive.type();
        String maskChar = sensitive.maskChar();

        switch (type) {
            case PASSWORD:
                return maskAll(strValue, maskChar);
            case PHONE:
                return maskPhone(strValue, maskChar);
            case EMAIL:
                return maskEmail(strValue, maskChar);
            case ID_CARD:
                return maskIdCard(strValue, maskChar);
            case BANK_CARD:
                return maskBankCard(strValue, maskChar);
            case NAME:
                return maskName(strValue, maskChar);
            case ADDRESS:
                return maskAddress(strValue, maskChar);
            case CUSTOM:
                return maskCustom(strValue, sensitive.prefixLen(), sensitive.suffixLen(), maskChar);
            default:
                return strValue;
        }
    }

    /**
     * 全部脱敏
     */
    private static String maskAll(String value, String maskChar) {
        return maskChar.repeat(Math.max(0, value.length()));
    }

    /**
     * 手机号脱敏：保留前3位和后4位
     */
    private static String maskPhone(String phone, String maskChar) {
        if (phone.length() < 7) {
            return maskAll(phone, maskChar);
        }
        return phone.substring(0, 3) + maskChar.repeat(Math.max(0, phone.length() - 7)) + phone.substring(phone.length() - 4);
    }

    /**
     * 邮箱脱敏：保留前3位和@后面的部分
     */
    private static String maskEmail(String email, String maskChar) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }
        String prefix = email.substring(0, Math.min(3, atIndex));
        String suffix = email.substring(atIndex);
        int maskLength = atIndex - prefix.length();
        return prefix + maskChar.repeat(Math.max(0, maskLength)) + suffix;
    }

    /**
     * 身份证脱敏：保留前6位和后4位
     */
    private static String maskIdCard(String idCard, String maskChar) {
        if (idCard.length() < 10) {
            return maskAll(idCard, maskChar);
        }
        return idCard.substring(0, 6) + maskChar.repeat(Math.max(0, idCard.length() - 10)) + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡脱敏：保留前4位和后4位
     */
    private static String maskBankCard(String bankCard, String maskChar) {
        if (bankCard.length() < 8) {
            return maskAll(bankCard, maskChar);
        }
        return bankCard.substring(0, 4) + maskChar.repeat(Math.max(0, bankCard.length() - 8)) + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 姓名脱敏：保留姓氏
     */
    private static String maskName(String name, String maskChar) {
        if (name.length() <= 1) {
            return name;
        }
        return name.charAt(0) + maskChar.repeat(Math.max(0, name.length() - 1));
    }

    /**
     * 地址脱敏：保留前6位（省市区）
     */
    private static String maskAddress(String address, String maskChar) {
        if (address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + maskChar.repeat(Math.max(0, Math.min(10, address.length() - 6)));
    }

    /**
     * 自定义脱敏
     */
    private static String maskCustom(String value, int prefixLen, int suffixLen, String maskChar) {
        if (value.length() <= prefixLen + suffixLen) {
            return maskAll(value, maskChar);
        }
        String prefix = value.substring(0, prefixLen);
        String suffix = value.substring(value.length() - suffixLen);
        int maskLength = value.length() - prefixLen - suffixLen;
        return prefix + maskChar.repeat(Math.max(0, maskLength)) + suffix;
    }

    /**
     * 判断是否为基本类型或包装类型
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Character.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Date.class ||
                clazz == java.time.LocalDateTime.class ||
                clazz == java.time.LocalDate.class ||
                clazz == java.time.LocalTime.class;
    }

    /**
     * 将对象序列化为JSON字符串并脱敏
     *
     * @param obj          对象
     * @param objectMapper ObjectMapper
     * @return 脱敏后的JSON字符串
     */
    public static String toJsonWithDesensitize(Object obj, ObjectMapper objectMapper) {
        try {
            Object desensitizedObj = desensitize(obj);
            return objectMapper.writeValueAsString(desensitizedObj);
        } catch (Exception e) {
            log.warn("序列化并脱敏失败: {}", e.getMessage());
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (Exception ex) {
                return "{}";
            }
        }
    }
}

