package com.stonebridge.tradeflow.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonebridge.tradeflow.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private static final String BEARER_PREFIX = "Bearer ";


    /**
     * 输出响应到 HttpServletResponse
     *
     * @param response HTTP 响应对象
     * @param result   响应数据
     */
    public static void out(HttpServletResponse response, Result result) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 使用 ObjectMapper 序列化 JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(result));
        } catch (IOException e) {
            logger.error("序列化响应失败: {}，结果: {}", e.getMessage(), result, e);
            throw new RuntimeException("响应写入失败", e);
        }
    }


    /**
     * Extracts the JWT token from the Authorization header of the HttpServletRequest.
     *
     * @param request the HttpServletRequest containing the Authorization header
     * @return the token if present and valid, or null if the header is missing or invalid
     */
    public static String getToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

}
