package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_role")
public class SysRole implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 角色id

    private String roleName;    // 角色名称

    private String roleCode;    // 角色编码

    private String description; // 描述

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间

    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}