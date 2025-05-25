package com.stonebridge.tradeflow.security.filter;

import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义无权限异常处理器，用于处理 Spring Security 中已认证但无权限的请求
 * 当用户尝试访问受保护资源（如通过 @PreAuthorize 限制的接口）但缺乏所需权限时，
 * 此处理器会返回 JSON 格式的错误响应，提示用户无权限访问
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    /**
     * 日志记录器，用于记录无权限访问的请求信息，便于调试和监控
     */
    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    /**
     * 处理无权限异常，返回 JSON 格式的错误响应
     *
     * @param request               HTTP 请求对象，包含客户端请求信息（如方法和 URI）
     * @param response              HTTP 响应对象，用于设置响应状态和内容
     * @param accessDeniedException 无权限异常，包含无权限的具体原因
     * @throws IOException 如果写入响应失败
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        // 记录无权限访问的日志，包括请求方法、URI 和异常原因
        logger.info("Handling AccessDeniedException: {} {}, reason: {}",
                request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("message", ResultCodeEnum.PERMISSION.getMessage());
        errorData.put("code", ResultCodeEnum.PERMISSION.getCode());
        // 使用 SecurityUtil 的 out 方法将 Result 对象序列化为 JSON 并写入响应输出流
        // 该方法内部使用 Jackson 进行序列化，确保响应格式统一
        SecurityUtil.out(response, Result.ok(errorData));
    }
}