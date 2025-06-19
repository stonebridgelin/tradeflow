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
    // 成功 - 通用成功状态
    SUCCESS(200, "Success", "Operation completed successfully"),
    // 失败 - 通用失败状态，建议细化具体错误
    FAIL(500, "Internal Server Error", "General failure status, recommend specifying detailed error"),


    // 认证与授权（400-499）
    // 未登录或认证失败 - 未提供有效认证信息（如无效或缺失 JWT token）
    LOGIN_AUTH(401, "Authentication Failed", "Invalid or missing authentication credentials (e.g., invalid or missing JWT token)"),
    // 无权限访问该接口 - 用户已认证但缺乏所需权限（如 @PreAuthorize 校验失败）
    PERMISSION(403, "Access Denied", "User is authenticated but lacks required permissions (e.g., @PreAuthorize validation failed)"),
    // 非法请求 - 请求格式或参数非法
    ILLEGAL_REQUEST(400, "Bad Request", "Invalid request format or parameters"),
    // 重复提交 - 短时间内重复提交相同请求
    REPEAT_SUBMIT(429, "Duplicate Submission", "Repeated submission of the same request in a short time"),
    // 参数校验异常 - 请求参数未通过验证（如 @Valid 失败）
    ARGUMENT_VALID_ERROR(400, "Parameter Validation Error", "Request parameters failed validation (e.g., @Valid failed)"),
    // 只允许 POST 请求 - 请求的 HTTP 方法不支持（如非 POST 访问登录接口）
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "HTTP method not supported for this endpoint (e.g., non-POST access to login endpoint)"),
    // 只允许 POST 请求 - 请求的 HTTP 方法不支持（如非 POST 访问登录接口）

    // 参数相关（400-419）
    // 缺少必要参数 - 请求缺少必要的参数
    MISSING_PARAMETER(400, "Missing Required Parameter", "Request is missing required parameters"),
    // 参数格式错误 - 请求参数格式不正确
    INVALID_PARAMETER(400, "Invalid Parameter Format", "Request parameter format is incorrect"),
    // 参数超出范围 - 请求参数值超出允许范围
    PARAMETER_OUT_OF_RANGE(400, "Parameter Out of Range", "Request parameter value is out of allowed range"),
    // 参数超出范围 - 请求参数值超出允许范围

    // 数据相关（404, 409）
    // 数据不存在 - 查询的数据记录不存在
    DATA_NOT_FOUND(404, "Data Not Found", "Requested data record does not exist"),
    // 数据已存在 - 尝试创建已存在的数据
    DATA_ALREADY_EXISTS(409, "Data Already Exists", "Attempting to create data that already exists"),


    // 账户相关（2100-2199）
    // 账号不存在 - 登录时提供的用户名不存在
    ACCOUNT_ERROR(2100, "Account Not Found", "Username provided during login does not exist"),
    // 密码不正确 - 登录时提供的密码错误
    PASSWORD_ERROR(2101, "Incorrect Password", "Password provided during login is incorrect"),
    // 账号不正确 - 手机号码或账号格式错误
    LOGIN_MOBILE_ERROR(2102, "Invalid Account", "Mobile number or account format is incorrect"),
    // 账号已停用 - 账号被禁用，无法登录
    ACCOUNT_STOP(2103, "Account Disabled", "Account has been disabled and cannot login"),
    // 账号已删除 - 账号已被删除，无法登录
    ACCOUNT_DELETE(2104, "Account Deleted", "Account has been deleted and cannot login"),
    // 账号已过期 - 账号有效期已过，无法登录
    ACCOUNT_EXPIRED(2105, "Account Expired", "Account validity period has expired and cannot login"),
    // 账号已锁定 - 账号因多次错误登录被锁定
    ACCOUNT_LOCKED(2106, "Account Locked", "Account has been locked due to multiple failed login attempts"),
    // 密码已过期 - 密码需要更新
    PASSWORD_EXPIRED(2107, "Password Expired", "Password needs to be updated"),


    // 业务相关（2200-2299）
    // 服务异常 - 后端服务内部错误（如数据库连接失败）
    SERVICE_ERROR(2200, "Service Error", "Backend service internal error (e.g., database connection failure)"),
    // 数据异常 - 数据处理或查询失败（如数据不一致）
    DATA_ERROR(2201, "Data Error", "Data processing or query failed (e.g., data inconsistency)"),
    // 该节点下有子节点，不可以删除 - 尝试删除包含子节点的节点
    NODE_ERROR(2202, "Cannot Delete Node with Children", "Attempting to delete a node that contains child nodes"),
    //业务逻辑错误 - 业务规则校验失败
    BUSINESS_LOGIC_ERROR(2203, "Business Logic Error", "Business rule validation failed"),
    // 外部服务调用失败 - 调用第三方服务时发生错误
    EXTERNAL_SERVICE_ERROR(2204, "External Service Error", "Error occurred while calling third-party service"),


    // 系统相关（2300-2399）
    // 系统繁忙 - 系统当前负载过高，请稍后重试
    SYSTEM_BUSY(2300, "System Busy", "System is currently under high load, please try again later"),
    // 系统维护中 - 系统正在维护，暂时无法提供服务
    SYSTEM_MAINTENANCE(2301, "System Under Maintenance", "System is currently under maintenance and temporarily unavailable"),
    // 请求频率超限 - 请求频率超出限制，请稍后重试
    RATE_LIMIT_EXCEEDED(2302, "Rate Limit Exceeded", "Request frequency exceeds limit, please try again later");

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
     *
     * @param code        错误码
     * @param message     错误消息
     * @param description 错误描述
     */
    ResultCodeEnum(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    /**
     * 根据错误码查找枚举实例
     *
     * @param code 错误码
     * @return 对应的 ResultCodeEnum 实例，若不存在则返回 null
     */
    public static ResultCodeEnum getByCode(Integer code) {
        return CODE_MAP.get(code);
    }

    /**
     * 检查错误码是否存在
     *
     * @param code 错误码
     * @return true 如果错误码存在，false 否则
     */
    public static boolean exists(Integer code) {
        return CODE_MAP.containsKey(code);
    }

    /**
     * 是否为成功状态
     *
     * @return true 如果是成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 是否为失败状态
     *
     * @return true 如果是失败状态
     */
    public boolean isFail() {
        return !isSuccess();
    }
}