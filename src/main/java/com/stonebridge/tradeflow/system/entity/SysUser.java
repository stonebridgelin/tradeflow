package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_user")
public class SysUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 会员id

    private String username;    // 用户名

    private String password;    // 密码

    private String name;        // 姓名

    private String phone;       // 手机

    private String headUrl;     // 头像地址

    private Long deptId;        // 部门id

    private Long postId;        // 岗位id

    private String description; // 描述

    private Boolean status;     // 状态（1：正常 0：停用）

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间

    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}