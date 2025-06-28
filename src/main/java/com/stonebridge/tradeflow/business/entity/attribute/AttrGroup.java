package com.stonebridge.tradeflow.business.entity.attribute;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * 属性分组实体类
 */
@Data
@TableName("pms_attr_group")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttrGroup {

    @TableId(type = IdType.AUTO)
    @TableField(value = "attr_group_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrGroupId;//分组id

    @TableField("attr_group_name")
    private String attrGroupName;//组名

    @TableField("sort")
    private Integer sort;//排序

    @TableField("description")
    private String description;//描述

    @TableField("icon")
    private String icon;//组图标

    @TableField(value = "category_id", typeHandler = StringToBigIntTypeHandler.class)
    private String categoryId;//所属分类id
}