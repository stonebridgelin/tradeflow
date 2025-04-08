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

    private Long roleId;        // 角色id

    private Long menuId;        // 菜单id

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间

    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}