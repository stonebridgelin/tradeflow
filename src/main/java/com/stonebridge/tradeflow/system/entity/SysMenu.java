package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;            // 编号
    private Long parentId;      // 所属上级
    private String name;        // 名称
    private Integer type;       // 类型(0:目录,1:菜单,2:按钮)
    private String path;        // 路由地址
    private String component;   // 组件路径
    private String perms;       // 权限标识
    private String icon;        // 图标
    private Integer sortValue;  // 排序
    private String status;     // 状态(0:禁止,1:正常)
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    @TableField(exist = false)
    private List<SysMenu> children;
    @TableField(exist = false)
    private boolean isSelect;

}