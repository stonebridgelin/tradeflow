package com.stonebridge.tradeflow.common.result;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误码枚举类，定义系统中使用的错误码和错误消息
 * 用于统一 API 响应格式，与 Result 类配合构建 JSON 响应
 * 错误码分组为通用、认证授权、账户相关、业务相关，便于维护
 */
@Getter
public enum ResultCodeEnum {

    // 通用状态（200-299）
    SUCCESS(200, "成功", "通用成功状态"),
    FAIL(500, "失败", "通用失败状态，建议细化具体错误"),

    // 认证与授权（400-499）
    LOGIN_AUTH(401, "未登录或认证失败", "未提供有效认证信息（如无效或缺失 JWT token）"),
    PERMISSION(403, "无权限访问该接口", "用户已认证但缺乏所需权限（如 @PreAuthorize 校验失败）"),
    ILLEGAL_REQUEST(400, "非法请求", "请求格式或参数非法"),
    REPEAT_SUBMIT(429, "重复提交", "短时间内重复提交相同请求"),
    ARGUMENT_VALID_ERROR(400, "参数校验异常", "请求参数未通过验证（如 @Valid 失败）"),
    METHOD_NOT_ALLOWED(405, "只允许 POST 请求", "请求的 HTTP 方法不支持（如非 POST 访问登录接口）"), // 新增

    // 账户相关（2100-2199）
    ACCOUNT_ERROR(2100, "账号不存在", "登录时提供的用户名不存在"),
    PASSWORD_ERROR(2101, "密码不正确", "登录时提供的密码错误"),
    LOGIN_MOBLE_ERROR(2102, "账号不正确", "手机号码或账号格式错误"),
    ACCOUNT_STOP(2103, "账号已停用", "账号被禁用，无法登录"),
    ACCOUNT_DELETE(2104, "账号已删除", "账号已被删除，无法登录"),

    // 业务相关（2200-2299）
    SERVICE_ERROR(2200, "服务异常", "后端服务内部错误（如数据库连接失败）"),
    DATA_ERROR(2201, "数据异常", "数据处理或查询失败（如数据不一致）"),
    NODE_ERROR(2202, "该节点下有子节点，不可以删除", "尝试删除包含子节点的节点");

    /**
     * 错误码，与 HTTP 状态码对齐或自定义
     */
    private final Integer code;

    /**
     * 错误消息，面向客户端的简洁描述
     */
    private final String message;

    /**
     * 错误描述，面向开发者的详细说明
     */
    private final String description;

    /**
     * 错误码映射，用于快速查找
     */
    private static final Map<Integer, ResultCodeEnum> CODE_MAP = new HashMap<>();

    // 静态初始化块，构建错误码映射
    static {
        for (ResultCodeEnum value : values()) {
            CODE_MAP.put(value.getCode(), value);
        }
    }

    /**
     * 构造函数，初始化错误码、消息和描述
     * @param code 错误码
     * @param message 错误消息
     * @param description 错误描述
     */
    ResultCodeEnum(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    /**
     * 根据错误码查找枚举实例
     * @param code 错误码
     * @return 对应的 ResultCodeEnum 实例，若不存在则返回 null
     */
    public static ResultCodeEnum getByCode(Integer code) {
        return CODE_MAP.get(code);
    }

    /**
     * 检查错误码是否存在
     * @param code 错误码
     * @return true 如果错误码存在，false 否则
     */
    public static boolean exists(Integer code) {
        return CODE_MAP.containsKey(code);
    }
}