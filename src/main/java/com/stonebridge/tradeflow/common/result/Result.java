package com.stonebridge.tradeflow.common.result;

import lombok.Data;


@Data
public class Result<T> {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private Long timestamp;

    /**
     * 请求追踪ID（可选，用于日志追踪）
     */
    private String traceId;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 基础构建方法 ====================

    /**
     * 构建基础结果对象
     */
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    /**
     * 构建完整结果对象
     */
    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 根据枚举构建结果对象
     */
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    // ==================== 成功响应方法 ====================

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> ok() {
        return build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    /**
     * 成功响应（带数据和自定义消息）
     */
    public static <T> Result<T> ok(T data, String message) {
        return build(data, ResultCodeEnum.SUCCESS.getCode(), message);
    }

    // ==================== 失败响应方法 ====================

    /**
     * 失败响应（使用默认失败状态）
     */
    public static <T> Result<T> fail() {
        return build(null, ResultCodeEnum.FAIL);
    }

    /**
     * 失败响应（带数据）
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }

    /**
     * 失败响应（指定错误码枚举）
     */
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        return build(null, resultCodeEnum);
    }

    /**
     * 失败响应（指定错误码枚举和数据）
     */
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum, T data) {
        return build(data, resultCodeEnum);
    }

    /**
     * 失败响应（自定义错误码和消息）
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return build(null, code, message);
    }

    /**
     * 失败响应（自定义错误码、消息和数据）
     */
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return build(data, code, message);
    }

    // ==================== 链式调用方法 ====================

    /**
     * 设置消息（链式调用）
     */
    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 设置错误码（链式调用）
     */
    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 设置数据（链式调用）
     */
    public Result<T> data(T data) {
        this.setData(data);
        return this;
    }

    /**
     * 设置追踪ID（链式调用）
     */
    public Result<T> traceId(String traceId) {
        this.setTraceId(traceId);
        return this;
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResultCodeEnum.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isFail() {
        return !isSuccess();
    }

    // ==================== 工具方法 ====================

    /**
     * 根据条件返回成功或失败
     */
    public static <T> Result<T> result(boolean success, T data) {
        return success ? ok(data) : fail();
    }

    /**
     * 根据条件返回成功或失败（带自定义失败信息）
     */
    public static <T> Result<T> result(boolean success, T data, ResultCodeEnum failEnum) {
        return success ? ok(data) : fail(failEnum);
    }

    /**
     * 从另一个Result复制状态（不复制数据）
     */
    public static <T> Result<T> copyStatus(Result<?> source) {
        Result<T> result = new Result<>();
        result.setCode(source.getCode());
        result.setMessage(source.getMessage());
        result.setTraceId(source.getTraceId());
        return result;
    }

    /**
     * 转换数据类型（保持状态不变）
     */
    public <R> Result<R> convertData(R newData) {
        Result<R> result = new Result<>();
        result.setCode(this.code);
        result.setMessage(this.message);
        result.setTimestamp(this.timestamp);
        result.setTraceId(this.traceId);
        result.setData(newData);
        return result;
    }
}