package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 日志主键
    private String title;       // 模块标题
    private String businessType;// 业务类型（0其它 1新增 2修改 3删除）
    private String method;      // 方法名称
    private String requestMethod;// 请求方式
    private String operatorType;// 操作类别（0其它 1后台用户 2手机端用户）
    private String operName;    // 操作人员
    private String deptName;    // 部门名称
    private String operUrl;     // 请求URL
    private String operIp;      // 主机地址
    private String operParam;   // 请求参数
    private String jsonResult;  // 返回参数
    private Integer status;     // 操作状态（0正常 1异常）
    private String errorMsg;    // 错误消息
    private Date operTime;      // 操作时间
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}