package com.stonebridge.tradeflow.business.entity.attribute;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * 商品属性实体类
 */
@Data
@TableName("pms_attr")
public class Attr {

    @TableField(value = "attr_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrId;//属性id

    @TableField("attr_name")
    private String attrName;//属性名

    @TableField("search_type")
    private Integer searchType;//是否需要检索[0-不需要，1-需要]

    @TableField("value_type")
    private Integer valueType;//值类型[0-单选，1-多选]

    @TableField("icon")
    private String icon;//属性图标

    @TableField("value_select")
    private String valueSelect;//可选值列表[用逗号分隔]

    @TableField("attr_type")
    private Integer attrType;//属性类型[0-销售属性，1-基本属性]

    @TableField("enable")
    private Integer enable;//启用状态[0-禁用，1-启用]

    @TableField(value = "category_id", typeHandler = StringToBigIntTypeHandler.class)
    private String categoryId;//所属分类

    @TableField("show_desc")
    private Integer showDesc;//快速展示【是否展示在介绍上；0-否 1-是】
}