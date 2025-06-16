package com.stonebridge.tradeflow.security.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

/**
 * Utility class for accessing Map objects stored in SecurityContextHolder.
 */
public class SecurityContextHolderUtil {

    /**
     * Retrieves the Map object stored in SecurityContextHolder.
     * @return Map<String, Object> if present, null otherwise.
     */
    public static Map<String, Object> getCurrentMap() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof Map) {
                return (Map<String, Object>) principal;
            }
            return null;
        } catch (Exception e) {
            // Log error in production (e.g., use SLF4J)
            System.err.println("Failed to get Map from SecurityContextHolder: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a value from the Map stored in SecurityContextHolder by key.
     * @param key The key to look up in the Map.
     * @return The value associated with the key, or null if not found or Map is null.
     */
    public static Object getValueByKey(String key) {
        Map<String, Object> map = getCurrentMap();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }
    public static String getUserId() {
        return getValueByKey("userId", String.class);
    }

    /**
     * Retrieves a typed value from the Map stored in SecurityContextHolder by key.
     * @param key The key to look up in the Map.
     * @param clazz The expected type of the value.
     * @param <T> The type parameter for the value.
     * @return The typed value associated with the key, or null if not found, Map is null, or type mismatch.
     */
    public static <T> T getValueByKey(String key, Class<T> clazz) {
        Object value = getValueByKey(key);
        if (value != null && clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }
}
