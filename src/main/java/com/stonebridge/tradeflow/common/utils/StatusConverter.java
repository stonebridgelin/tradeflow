package com.stonebridge.tradeflow.common.utils;

import java.util.HashMap;
import java.util.Map;

public class StatusConverter {
    private static final Map<String, String> statusMap = new HashMap<>();
    private static final Map<String, String> typeMap = new HashMap<>();
    private static final Map<String, String> menuStatusMap = new HashMap<>();


    static {
        statusMap.put("0", "正常");
        statusMap.put("1", "停用");
        statusMap.put("2", "离职");

        typeMap.put("0", "一级目录");
        typeMap.put("1", "二级目录");
        typeMap.put("2", "按钮");

        menuStatusMap.put("0", "禁止");
        menuStatusMap.put("1", "正常");
    }

    public static String getStatusDescription(String code) {
        return statusMap.getOrDefault(code, "未知状态");
    }

    public static String getTypeDescription(String code) {
        return typeMap.getOrDefault(code, "未知状态");
    }

    public static String getMenuStatusDescription(String code) {
        return menuStatusMap.getOrDefault(code, "未知状态");
    }
}

