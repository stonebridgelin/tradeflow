package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_dept")
public class SysDept implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 主键ID

    private String name;        // 部门名称

    private Long parentId;      // 上级部门id

    private String treePath;    // 树结构

    private Integer sortValue;  // 排序

    private String leader;      // 负责人

    private String phone;      // 电话

    private Boolean status;     // 状态（1正常 0停用）

    private Timestamp createTime; // 创建时间

    private Timestamp updateTime; // 更新时间

    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}