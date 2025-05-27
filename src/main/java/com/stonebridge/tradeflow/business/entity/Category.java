package com.stonebridge.tradeflow.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.stonebridge.tradeflow.business.typehandler.LongToStringTypeHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类实体类
 */
@Data
@TableName("pms_category")
public class Category {
    /**
     * 分类id
     */
    @TableId(type = IdType.AUTO)
    @TableField(typeHandler = LongToStringTypeHandler.class)
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 父分类id
     */
    private Long parentId;

    /**
     * 是否显示[0-不显示，1-显示]
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Category> children;

    @TableField(exist = false)
    private boolean hasChildren;
}