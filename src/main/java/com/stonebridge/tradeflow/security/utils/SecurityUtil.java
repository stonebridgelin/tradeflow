package com.stonebridge.tradeflow.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SecurityUtil {
    private static final String BEARER_PREFIX = "Bearer ";


    public static void out(HttpServletResponse response, Result result) {
        ObjectMapper objectMapper = new ObjectMapper();
        //封装response的状态码和内容格式
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //内容：result json
        try {
            //使用jackson，把json格式的result写入到response的输出流中
            objectMapper.writeValue(response.getOutputStream(), result);
        } catch (IOException e) {
            e.printStackTrace();
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
