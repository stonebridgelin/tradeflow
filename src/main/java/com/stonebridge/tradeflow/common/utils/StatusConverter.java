package com.stonebridge.tradeflow.common.utils;

import java.util.HashMap;
import java.util.Map;

public class StatusConverter {
    private static final Map<String, String> statusMap = new HashMap<>();

    static {
        statusMap.put("0", "正常");
        statusMap.put("1", "停用");
        statusMap.put("2", "离职");
    }

    public static String getStatusDescription(String code) {
        return statusMap.getOrDefault(code, "未知状态");
    }
}

