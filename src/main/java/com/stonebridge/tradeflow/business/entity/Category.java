package com.stonebridge.tradeflow.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.stonebridge.tradeflow.business.typehandler.LongToStringTypeHandler;

import java.util.Date;
import java.util.List;

/**
 * 商品分类实体类
 */
@Data
@TableName("pms_category")
public class Category {
    @TableId(type = IdType.AUTO)
    @TableField(typeHandler = LongToStringTypeHandler.class)
    private String id;//分类id
    private String name;//分类名称
    @TableField(typeHandler = LongToStringTypeHandler.class)
    private String parentId;//父分类id
    private Integer status;//是否显示[0-不显示，1-显示]
    private Integer level;//等级，分别有1，2，3。等级越小越接近根目录
    private Integer orderNum;//排序
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    @TableField(exist = false)
    private List<Category> children;
    @TableField(exist = false)
    private boolean hasChildren;
}