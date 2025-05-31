package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 访问ID
    private String username;    // 用户账号
    private String ipaddr;      // 登录IP地址
    private Boolean status;     // 登录状态（0成功 1失败）
    private String msg;         // 提示信息
    private Date accessTime;    // 访问时间
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间
    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}