package com.stonebridge.tradeflow.system.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MenuDto implements Serializable {
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
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> props; // 路由props配置（JSON对象）
}
