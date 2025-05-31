package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 主键id
    private Long roleId;        // 角色id
    private Long userId;        // 用户id
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间

}