package com.stonebridge.tradeflow.common.vo;

import lombok.Data;

@Data
public class Result<T> {

    public static final String successMsg = "Success";
    public static final String errorMsg = "Error";

    /**
     * 状态码（200 表示成功，500 表示失败）
     */
    public static final int successCode = 200;
    public static final int errorCode = 500;

    private int code;    // 状态码
    private String msg;  // 消息
    private T data;      // 数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(successCode);
        result.setMsg(successMsg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(errorCode);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
