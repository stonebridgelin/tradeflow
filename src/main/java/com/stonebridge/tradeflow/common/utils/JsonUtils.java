package com.stonebridge.tradeflow.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 基于Jackson的JSON工具类，提供简单易用的JSON操作方法
 *
 * @author YourName
 * @date 2025-06-18
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 配置ObjectMapper
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 支持Java 8时间类型
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        // 设置日期格式
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON字符串失败", e);
            throw new RuntimeException("对象转JSON字符串失败", e);
        }
    }

    /**
     * 对象转JSON字符串（格式化输出）
     */
    public static String toJsonPrettyString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转格式化JSON字符串失败", e);
            throw new RuntimeException("对象转格式化JSON字符串失败", e);
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error("JSON字符串转对象失败，json: {}, class: {}", jsonString, clazz.getSimpleName(), e);
            throw new RuntimeException("JSON字符串转对象失败", e);
        }
    }

    /**
     * JSON字符串转对象（使用TypeReference处理泛型）
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> typeReference) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (IOException e) {
            log.error("JSON字符串转对象失败，json: {}", jsonString, e);
            throw new RuntimeException("JSON字符串转对象失败", e);
        }
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("JSON字符串转List失败，json: {}, class: {}", jsonString, clazz.getSimpleName(), e);
            throw new RuntimeException("JSON字符串转List失败", e);
        }
    }

    /**
     * JSON字符串转Map
     */
    public static Map<String, Object> parseMap(String jsonString) {
        return parseObject(jsonString, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * JSON字符串转指定类型的Map
     */
    public static <K, V> Map<K, V> parseMap(String jsonString, Class<K> keyClass, Class<V> valueClass) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString,
                    OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (IOException e) {
            log.error("JSON字符串转Map失败，json: {}", jsonString, e);
            throw new RuntimeException("JSON字符串转Map失败", e);
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Map转对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(map, clazz);
    }

    /**
     * 获取JsonNode
     */
    public static JsonNode parseTree(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(jsonString);
        } catch (IOException e) {
            log.error("解析JSON树失败，json: {}", jsonString, e);
            throw new RuntimeException("解析JSON树失败", e);
        }
    }

    /**
     * 检查字符串是否为有效的JSON
     */
    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(jsonString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取JSON字符串中指定路径的值
     */
    public static String getValueByPath(String jsonString, String path) {
        JsonNode jsonNode = parseTree(jsonString);
        if (jsonNode == null) {
            return null;
        }

        String[] paths = path.split("\\.");
        JsonNode currentNode = jsonNode;

        for (String p : paths) {
            if (currentNode.isArray() && p.matches("\\d+")) {
                currentNode = currentNode.get(Integer.parseInt(p));
            } else {
                currentNode = currentNode.get(p);
            }
            if (currentNode == null) {
                return null;
            }
        }

        return currentNode.isTextual() ? currentNode.asText() : currentNode.toString();
    }

    /**
     * 深拷贝对象（通过JSON序列化和反序列化）
     */
    public static <T> T deepCopy(T obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        String jsonString = toJsonString(obj);
        return parseObject(jsonString, clazz);
    }

    /**
     * 安全的JSON转换（不抛异常，返回默认值）
     */
    public static <T> T parseObjectSafely(String jsonString, Class<T> clazz, T defaultValue) {
        try {
            T result = parseObject(jsonString, clazz);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.warn("JSON转换失败，返回默认值，json: {}, class: {}", jsonString, clazz.getSimpleName(), e);
            return defaultValue;
        }
    }

    /**
     * 安全的对象转JSON（不抛异常，返回默认值）
     */
    public static String toJsonStringSafely(Object obj, String defaultValue) {
        try {
            String result = toJsonString(obj);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            log.warn("对象转JSON失败，返回默认值", e);
            return defaultValue;
        }
    }
}
