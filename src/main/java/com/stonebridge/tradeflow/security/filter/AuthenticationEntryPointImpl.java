package com.stonebridge.tradeflow.security.filter;

import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.security.utils.SecurityUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义未认证异常处理器，用于处理 Spring Security 中未认证的请求
 * 当用户未登录或认证失败（如缺少有效 JWT token）尝试访问受保护资源时，
 * 此处理器会返回 JSON 格式的错误响应，提示用户需要登录
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /**
     * 处理未认证异常，返回 JSON 格式的错误响应
     *
     * @param request       HTTP 请求对象，包含客户端请求信息
     * @param response      HTTP 响应对象，用于设置响应状态和内容
     * @param authException 认证异常，包含未认证的具体原因
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // 设置响应状态码
        // 使用 HTTP 200（OK）而非 401（Unauthorized），可能是为了与前端约定一致
        // 注意：通常建议使用 401 表示未认证，需根据项目规范调整
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("message", ResultCodeEnum.LOGIN_AUTH.getMessage());
        errorData.put("code", ResultCodeEnum.LOGIN_AUTH.getCode());
        // 构建错误响应对象
        // 使用 Result 类封装响应数据，包含错误码和错误消息
        // ResultCodeEnum.LOGIN_AUTH 提供未认证的错误码和消息（如 401 和“未登录”）
        Result result = Result.ok(errorData);
        // 输出响应，设置状态码为 401
        SecurityUtil.out(response, result);
    }
}