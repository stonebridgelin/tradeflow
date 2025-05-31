package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("sys_post")
public class SysPost implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 岗位ID
    private String postCode;    // 岗位编码
    private String name;        // 岗位名称
    private String description; // 描述
    private Boolean status;     // 状态（1正常 0停用）
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间
    @TableLogic
    private Integer isDeleted;  // 删除标记（0:可用 1:已删除）
}