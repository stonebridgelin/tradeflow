package com.stonebridge.tradeflow.common.exception;

import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * 全局异常、特定异常、自定义异常处理的优先顺序等级是：自定义异常处理>特定异常>全局异常
 * 开发中要抛出自定义异常时
 *
 * @GetMapping("/findAll")
 * @Operation(summary = "获取所有角色信息", description = "获取所有角色信息")
 * public List<SysRole> findAll() {
 * try {
 * int a = 10 / 0;
 * } catch (Exception e) {
 * throw new MyException(ResultCodeEnum.ACCOUNT_STOP);
 * }
 * return sysRoleService.list();
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //1.处理全局异常
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        //执行了全局异常处理
        e.printStackTrace();
        return Result.fail();
    }

    //2.处理特定异常
    @ExceptionHandler(ArithmeticException.class)
    public Result error(ArithmeticException e) {
        e.printStackTrace();
        //执行了特定异常处理
        return Result.fail();
    }

    //3.处理自定义异常
    @ExceptionHandler(CustomizeException.class)
    public Result error(CustomizeException e) {
        e.printStackTrace();
        return Result.fail().message(e.getMessage()).code(e.getCode());
    }
}
