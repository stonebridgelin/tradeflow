package com.stonebridge.tradeflow.security.utils;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            JSONObject json = new JSONObject(result);
            response.getWriter().write(json.toString());
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
