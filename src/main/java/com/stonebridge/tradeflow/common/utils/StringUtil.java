package com.stonebridge.tradeflow.common.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 字符串工具类
 * 参考hutool设计理念，提供常用的字符串处理方法
 *
 * <h3>方法使用示例</h3>
 * <pre>
 * // 判空方法
 * StringUtil.isEmpty(null)                    → true
 * StringUtil.isNotEmpty("hello")              → true
 * StringUtil.isBlank("  ")                    → true
 * StringUtil.isNotBlank("hello")              → true
 *
 * // 默认值处理
 * StringUtil.nullToEmpty(null)                → ""
 * StringUtil.emptyToDefault("", "默认")        → "默认"
 * StringUtil.blankToDefault("  ", "默认")      → "默认"
 *
 * // 去空白处理
 * StringUtil.trim("  hello  ")                → "hello"
 * StringUtil.trim(123)                        → "123"
 * StringUtil.trimToNull("  ")                 → null
 * StringUtil.trimToEmpty(null)                → ""
 *
 * // 字符串格式化
 * StringUtil.format("{}岁", 18)               → "18岁"
 * StringUtil.format("{}+{}={}", 1, 2, 3)      → "1+2=3"
 *
 * // 命名转换
 * StringUtil.camelToUnderline("userName")     → "user_name"
 * StringUtil.underlineToCamel("user_name")    → "userName"
 * StringUtil.capitalize("hello")              → "Hello"
 * StringUtil.uncapitalize("Hello")            → "hello"
 *
 * // 字符串操作
 * StringUtil.repeat("*", 3)                   → "***"
 * StringUtil.padLeft("123", 5, '0')           → "00123"
 * StringUtil.padRight("123", 5, '0')          → "12300"
 *
 * // 截取操作
 * StringUtil.sub("hello", 1, 3)               → "el"
 * StringUtil.left("hello", 3)                 → "hel"
 * StringUtil.right("hello", 3)                → "llo"
 *
 * // 前后缀处理
 * StringUtil.removePrefix("hello.txt", "hello") → ".txt"
 * StringUtil.removeSuffix("hello.txt", ".txt")  → "hello"
 *
 * // 其他操作
 * StringUtil.reverse("hello")                 → "olleh"
 * StringUtil.similarity("hello", "hallo")     → 0.8
 *
 * // 字符串拼接
 * StringUtil.join(",", "a", "b", "c")         → "a,b,c"
 * StringUtil.joinNotBlank(",", "a", "", "b")  → "a,b"
 *
 * // 分割操作
 * StringUtil.split("a,b,c", ",")              → ["a", "b", "c"]
 *
 * // 包装操作
 * StringUtil.wrap("hello", "'")               → "'hello'"
 * StringUtil.unwrap("'hello'", "'")           → "hello"
 *
 * // 中文处理
 * StringUtil.containsChinese("hello世界")      → true
 * StringUtil.getByteLength("hello世界")        → 9
 *
 * // 隐藏信息
 * StringUtil.hide("13812345678", 3, 4, '*')   → "138****5678"
 *
 * // 随机字符串
 * StringUtil.random(6)                        → "aBc123" (随机)
 * StringUtil.random(4, "0123456789")          → "1234" (随机)
 *
 * // 参数解析
 * Map<String, Object> params = Map.of("page", "2", "limit", "50", "enabled", "true");
 * StringUtil.parseIntParameter(params, "page", 1, 1, 100)     → 2
 * StringUtil.parseStringParameter(params, "name", "默认名称")   → "默认名称"
 * StringUtil.parseBooleanParameter(params, "enabled", false)   → true
 * </pre>
 *
 * @author YourName
 * @since 1.0.0
 */
@Slf4j
public class StringUtil {

    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";

    // 常用正则表达式
    private static final Pattern CAMEL_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("_([a-z])");

    /**
     * 判断字符串是否为null或空字符串
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为null且不为空字符串
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串或只包含空白字符）
     */
    public static boolean isBlank(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 如果字符串为null，返回空字符串，否则返回原字符串
     */
    public static String nullToEmpty(String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * 如果字符串为空或null，返回默认值
     */
    public static String emptyToDefault(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * 如果字符串为空白或null，返回默认值
     */
    public static String blankToDefault(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    /**
     * 去除字符串两端的空白字符，null安全
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除对象转字符串后两端的空白字符，null安全
     * 适用于处理各种类型的对象
     */
    public static String trim(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString().trim();
    }

    /**
     * 去除字符串两端的空白字符，如果结果为空字符串则返回null
     */
    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isEmpty(trimmed) ? null : trimmed;
    }

    /**
     * 去除字符串两端的空白字符，如果为null则返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 字符串格式化，支持{}占位符
     */
    public static String format(String template, Object... params) {
        if (isEmpty(template) || params == null || params.length == 0) {
            return template;
        }

        StringBuilder sb = new StringBuilder(template.length() + 50);
        int paramIndex = 0;
        int start = 0;
        int pos;

        while ((pos = template.indexOf("{}", start)) != -1) {
            sb.append(template, start, pos);
            if (paramIndex < params.length) {
                sb.append(params[paramIndex++]);
            } else {
                sb.append("{}");
            }
            start = pos + 2;
        }

        sb.append(template.substring(start));
        return sb.toString();
    }

    /**
     * 驼峰命名转下划线命名
     */
    public static String camelToUnderline(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return CAMEL_PATTERN.matcher(str)
                .replaceAll(m -> "_" + m.group().toLowerCase());
    }

    /**
     * 下划线命名转驼峰命名
     */
    public static String underlineToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return UNDERLINE_PATTERN.matcher(str.toLowerCase())
                .replaceAll(m -> m.group(1).toUpperCase());
    }

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
     * 重复字符串
     */
    public static String repeat(String str, int times) {
        if (str == null || times <= 0) {
            return EMPTY;
        }
        if (times == 1) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 左侧填充
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = EMPTY;
        }
        if (str.length() >= length) {
            return str;
        }
        return repeat(String.valueOf(padChar), length - str.length()) + str;
    }

    /**
     * 右侧填充
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = EMPTY;
        }
        if (str.length() >= length) {
            return str;
        }
        return str + repeat(String.valueOf(padChar), length - str.length());
    }

    /**
     * 截取字符串，支持负数索引
     */
    public static String sub(String str, int start, int end) {
        if (isEmpty(str)) {
            return str;
        }

        int len = str.length();

        if (start < 0) {
            start = len + start;
        }
        if (end < 0) {
            end = len + end;
        }

        start = Math.max(0, start);
        end = Math.min(len, end);

        if (start >= end) {
            return EMPTY;
        }

        return str.substring(start, end);
    }

    /**
     * 从左侧截取指定长度的字符串
     */
    public static String left(String str, int len) {
        if (isEmpty(str) || len <= 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * 从右侧截取指定长度的字符串
     */
    public static String right(String str, int len) {
        if (isEmpty(str) || len <= 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * 移除字符串前缀，忽略大小写
     */
    public static String removePrefix(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str;
        }
        if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
            return str.substring(prefix.length());
        }
        return str;
    }

    /**
     * 移除字符串后缀，忽略大小写
     */
    public static String removeSuffix(String str, String suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str;
        }
        if (str.toLowerCase().endsWith(suffix.toLowerCase())) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
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

    /**
     * 计算两个字符串的相似度（编辑距离算法）
     */
    public static double similarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }
        if (str1.equals(str2)) {
            return 1.0;
        }

        int maxLen = Math.max(str1.length(), str2.length());
        if (maxLen == 0) {
            return 1.0;
        }

        return 1.0 - (double) editDistance(str1, str2) / maxLen;
    }

    /**
     * 计算编辑距离
     */
    private static int editDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]),
                            dp[i - 1][j - 1]
                    ) + 1;
                }
            }
        }

        return dp[len1][len2];
    }

    /**
     * 字符串拼接，跳过null和空字符串
     */
    public static String join(String delimiter, String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }

        return Arrays.stream(strs)
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 字符串拼接，跳过null和空白字符串
     */
    public static String joinNotBlank(String delimiter, String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }

        return Arrays.stream(strs)
                .filter(StringUtil::isNotBlank)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 分割字符串并去除空白
     */
    public static List<String> split(String str, String delimiter) {
        if (isEmpty(str)) {
            return new ArrayList<>();
        }

        return Arrays.stream(str.split(delimiter))
                .map(String::trim)
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList());
    }

    /**
     * 包装字符串，在两端添加指定字符
     */
    public static String wrap(String str, String wrapper) {
        if (isEmpty(str) || wrapper == null) {
            return str;
        }
        return wrapper + str + wrapper;
    }

    /**
     * 移除包装字符
     */
    public static String unwrap(String str, String wrapper) {
        if (isEmpty(str) || isEmpty(wrapper)) {
            return str;
        }

        if (str.startsWith(wrapper) && str.endsWith(wrapper)
                && str.length() >= wrapper.length() * 2) {
            return str.substring(wrapper.length(), str.length() - wrapper.length());
        }
        return str;
    }

    /**
     * 判断字符串是否包含中文字符
     */
    public static boolean containsChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (c >= 0x4e00 && c <= 0x9fa5) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串的字节长度（中文字符按2个字节计算）
     */
    public static int getByteLength(String str) {
        if (isEmpty(str)) {
            return 0;
        }

        int length = 0;
        for (char c : str.toCharArray()) {
            if (c >= 0x4e00 && c <= 0x9fa5) {
                length += 2; // 中文字符
            } else {
                length += 1; // 其他字符
            }
        }
        return length;
    }

    /**
     * 隐藏字符串中间部分，常用于手机号、身份证号等敏感信息处理
     */
    public static String hide(String str, int startLen, int endLen, char hideChar) {
        if (isEmpty(str)) {
            return str;
        }

        int totalLen = str.length();
        if (startLen + endLen >= totalLen) {
            return str;
        }

        String start = str.substring(0, startLen);
        String end = str.substring(totalLen - endLen);
        String middle = repeat(String.valueOf(hideChar), totalLen - startLen - endLen);

        return start + middle + end;
    }

    /**
     * 生成随机字符串
     */
    public static String random(int length) {
        return random(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    /**
     * 从指定字符集生成随机字符串
     */
    public static String random(int length, String chars) {
        if (length <= 0 || isEmpty(chars)) {
            return EMPTY;
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // ================================= 参数解析相关方法 =================================

    /**
     * 从Map中解析整数参数，支持默认值和边界值限制
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @param minValue    最小值
     * @param maxValue    最大值
     * @return 解析后的整数值，在指定范围内
     */
    public static int parseIntParameter(Map<String, Object> params, String paramName, int defaultValue, int minValue, int maxValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .filter(StringUtil::isNotBlank)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        log.warn("参数 {} 格式错误: {}, 使用默认值: {}", paramName, value, defaultValue);
                        return defaultValue;
                    }
                })
                .map(value -> Math.max(minValue, Math.min(maxValue, value)))
                .orElse(defaultValue);
    }

    /**
     * 从Map中解析长整数参数，支持默认值和边界值限制
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @param minValue    最小值
     * @param maxValue    最大值
     * @return 解析后的长整数值，在指定范围内
     */
    public static long parseLongParameter(Map<String, Object> params, String paramName, long defaultValue, long minValue, long maxValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .filter(StringUtil::isNotBlank)
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        log.warn("参数 {} 格式错误: {}, 使用默认值: {}", paramName, value, defaultValue);
                        return defaultValue;
                    }
                })
                .map(value -> Math.max(minValue, Math.min(maxValue, value)))
                .orElse(defaultValue);
    }

    /**
     * 从Map中解析字符串参数，支持默认值和长度限制
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @param maxLength   最大长度（0表示不限制）
     * @return 解析后的字符串，去除首尾空白并限制长度
     */
    public static String parseStringParameter(Map<String, Object> params, String paramName, String defaultValue, int maxLength) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .map(StringUtil::trim)
                .filter(StringUtil::isNotBlank)
                .map(value -> maxLength > 0 && value.length() > maxLength ? value.substring(0, maxLength) : value)
                .orElse(defaultValue);
    }

    /**
     * 从Map中解析字符串参数（无长度限制）
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @return 解析后的字符串，去除首尾空白
     */
    public static String parseStringParameter(Map<String, Object> params, String paramName, String defaultValue) {
        return parseStringParameter(params, paramName, defaultValue, 0);
    }

    /**
     * 从Map中解析布尔参数
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @return 解析后的布尔值
     */
    public static boolean parseBooleanParameter(Map<String, Object> params, String paramName, boolean defaultValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .map(StringUtil::trim)
                .filter(StringUtil::isNotBlank)
                .map(value -> {
                    String lowerValue = value.toLowerCase();
                    if ("true".equals(lowerValue) || "1".equals(lowerValue) || "yes".equals(lowerValue)) {
                        return true;
                    } else if ("false".equals(lowerValue) || "0".equals(lowerValue) || "no".equals(lowerValue)) {
                        return false;
                    } else {
                        log.warn("参数 {} 格式错误: {}, 使用默认值: {}", paramName, value, defaultValue);
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    /**
     * 从Map中解析双精度浮点数参数，支持默认值和边界值限制
     *
     * @param params      参数Map
     * @param paramName   参数名
     * @param defaultValue 默认值
     * @param minValue    最小值
     * @param maxValue    最大值
     * @return 解析后的双精度浮点数值，在指定范围内
     */
    public static double parseDoubleParameter(Map<String, Object> params, String paramName, double defaultValue, double minValue, double maxValue) {
        return Optional.ofNullable(params.get(paramName))
                .map(Object::toString)
                .filter(StringUtil::isNotBlank)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        log.warn("参数 {} 格式错误: {}, 使用默认值: {}", paramName, value, defaultValue);
                        return defaultValue;
                    }
                })
                .map(value -> Math.max(minValue, Math.min(maxValue, value)))
                .orElse(defaultValue);
    }
}