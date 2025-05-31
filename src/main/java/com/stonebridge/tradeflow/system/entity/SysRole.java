package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
}