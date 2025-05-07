package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 主键ID

    private String roleId;        // 角色id

    private String menuId;        // 菜单id

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间
}