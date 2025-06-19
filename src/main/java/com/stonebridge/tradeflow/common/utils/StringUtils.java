package com.stonebridge.tradeflow.common.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * 字符串工具类
 * 提供常用的字符串操作方法
 *
 * @author Generated
 * @version 1.0
 */
public class StringUtils {

//    主要功能模块
//1. 基础判断方法
//    isEmpty() / isNotEmpty() - 判断字符串是否为空
//    isBlank() / isNotBlank() - 判断字符串是否为空白
//    isAllNotEmpty() / isAnyEmpty() - 批量判断
//
//2. 字符串处理
//    trim() / trimToNull() / trimToEmpty() - 去空白处理
//    defaultIfNull() / defaultIfEmpty() / defaultIfBlank() - 默认值处理
//
//3. 字符串转换
//    capitalize() / uncapitalize() - 首字母大小写
//    camelToUnderscore() / underscoreToCamel() - 命名风格转换
//    reverse() - 字符串反转
//
//4. 截取和填充
//    left() / right() - 左右截取
//    leftPad() / rightPad() - 左右填充
//    repeat() - 重复字符串
//
//5. 比较和查找
//    equals() / equalsIgnoreCase() - 安全比较
//    containsIgnoreCase() - 忽略大小写包含
//    startsWithIgnoreCase() / endsWithIgnoreCase() - 忽略大小写前后缀
//
//6. 分割和连接
//    splitAndTrim() - 分割并去空白
//    join() - 连接数组或集合
//
//7. 数据验证
//    isValidEmail() - 邮箱验证
//    isValidPhone() - 手机号验证（中国大陆）
//    isValidIdCard() - 身份证验证
//    isNumeric() / isInteger() - 数字验证
//
//8. 数据脱敏
//    maskPhone() - 手机号脱敏
//    maskEmail() - 邮箱脱敏
//    maskIdCard() - 身份证脱敏
//    maskBankCard() - 银行卡脱敏
//
//9. 编码相关
//    md5() - MD5加密
//    randomString() / randomNumeric() - 随机字符串生成
//
//10. 其他实用功能
//    countOccurrences() - 统计子字符串出现次数
//    deleteWhitespace() - 删除所有空白字符
//    removeStart() / removeEnd() - 移除前后缀
//    getByteLength() - 获取字节长度

    private static final String EMPTY = "";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^\\d{17}[\\dXx]$");

    /**
     * 私有构造器，防止实例化
     */
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ========== 基础判断方法 ==========

    /**
     * 判断字符串是否为空(null 或 "")
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白(null、""、或只包含空白字符)
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断所有字符串是否都不为空
     */
    public static boolean isAllNotEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断任意一个字符串是否为空
     */
    public static boolean isAnyEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        for (String str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    // ========== 字符串处理方法 ==========

    /**
     * 去除字符串两端空白，如果为null则返回null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除字符串两端空白，如果为null或trim后为空则返回null
     */
    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isEmpty(trimmed) ? null : trimmed;
    }

    /**
     * 去除字符串两端空白，如果为null或trim后为空则返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 如果字符串为null，返回默认值
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 如果字符串为空，返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 如果字符串为空白，返回默认值
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    // ========== 字符串转换方法 ==========

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderscore(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    /**
     * 下划线转驼峰
     */
    public static String underscoreToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        String[] parts = str.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i].toLowerCase());
            } else {
                result.append(capitalize(parts[i].toLowerCase()));
            }
        }
        return result.toString();
    }

    /**
     * 反转字符串
     */
    public static String reverse(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }

    // ========== 字符串截取和填充 ==========

    /**
     * 从左边截取指定长度的字符串
     */
    public static String left(String str, int len) {
        if (str == null || len < 0) {
            return str;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * 从右边截取指定长度的字符串
     */
    public static String right(String str, int len) {
        if (str == null || len < 0) {
            return str;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * 左填充字符串到指定长度
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int padLen = size - str.length();
        if (padLen <= 0) {
            return str;
        }
        return repeat(padChar, padLen) + str;
    }

    /**
     * 右填充字符串到指定长度
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int padLen = size - str.length();
        if (padLen <= 0) {
            return str;
        }
        return str + repeat(padChar, padLen);
    }

    /**
     * 重复字符指定次数
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 重复字符串指定次数
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    // ========== 字符串比较方法 ==========

    /**
     * 安全的字符串比较（处理null值）
     */
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    /**
     * 忽略大小写的字符串比较
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 检查字符串是否包含指定子字符串（忽略大小写）
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    /**
     * 检查字符串是否以指定前缀开始（忽略大小写）
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * 检查字符串是否以指定后缀结束（忽略大小写）
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }

    // ========== 字符串分割和连接 ==========

    /**
     * 分割字符串并去除空白元素
     */
    public static String[] splitAndTrim(String str, String separator) {
        if (isEmpty(str)) {
            return new String[0];
        }
        String[] parts = str.split(separator);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = trim(part);
            if (isNotEmpty(trimmed)) {
                result.add(trimmed);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * 使用指定分隔符连接字符串数组
     */
    public static String join(String[] array, String separator) {
        if (array == null || array.length == 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * 使用指定分隔符连接集合
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object obj : collection) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(obj);
            first = false;
        }
        return sb.toString();
    }

    // ========== 数据验证方法 ==========

    /**
     * 验证是否为有效的邮箱地址
     */
    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证是否为有效的手机号码（中国大陆）
     */
    public static boolean isValidPhone(String phone) {
        return isNotBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证是否为有效的身份证号码（中国大陆）
     */
    public static boolean isValidIdCard(String idCard) {
        return isNotBlank(idCard) && ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 验证是否为数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证是否为整数
     */
    public static boolean isInteger(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ========== 字符串脱敏方法 ==========

    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        if (username.length() <= 2) {
            return email;
        }
        return username.substring(0, 2) + "****@" + parts[1];
    }

    /**
     * 身份证号脱敏
     */
    public static String maskIdCard(String idCard) {
        if (isEmpty(idCard) || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 银行卡号脱敏
     */
    public static String maskBankCard(String bankCard) {
        if (isEmpty(bankCard) || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "****" + bankCard.substring(bankCard.length() - 4);
    }

    // ========== 编码相关方法 ==========

    /**
     * 计算字符串的MD5值
     */
    public static String md5(String str) {
        if (isEmpty(str)) {
            return str;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * 生成随机字符串
     */
    public static String randomString(int length) {
        if (length <= 0) {
            return EMPTY;
        }
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机数字字符串
     */
    public static String randomNumeric(int length) {
        if (length <= 0) {
            return EMPTY;
        }
        String chars = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ========== 其他实用方法 ==========

    /**
     * 统计子字符串在字符串中出现的次数
     */
    public static int countOccurrences(String str, String subStr) {
        if (isEmpty(str) || isEmpty(subStr)) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }
        return count;
    }

    /**
     * 删除字符串中的所有空白字符
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s", "");
    }

    /**
     * 移除字符串开头的指定前缀
     */
    public static String removeStart(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str;
        }
        if (str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }

    /**
     * 移除字符串结尾的指定后缀
     */
    public static String removeEnd(String str, String suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str;
        }
        if (str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
    }

    /**
     * 获取字符串的字节长度（UTF-8编码）
     */
    public static int getByteLength(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 将字符串转换为驼峰命名规则的类名
     */
    public static String toClassName(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return capitalize(underscoreToCamel(str));
    }
}
