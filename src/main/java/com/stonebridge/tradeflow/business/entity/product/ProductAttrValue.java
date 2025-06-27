package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * SPU属性值实体类
 */
@Data
@TableName("pms_product_attr_value")
public class ProductAttrValue {
    @TableField(value = "id", typeHandler = StringToBigIntTypeHandler.class)
    private String id;//主键ID

    @TableField(value = "spu_id", typeHandler = StringToBigIntTypeHandler.class)
    private String spuId;//商品id

    @TableField(value = "attr_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrId;//属性id

    @TableField("attr_name")
    private String attrName;//属性名

    @TableField("attr_value")
    private String attrValue;//属性值

    @TableField("attr_sort")
    private Integer attrSort;//顺序

    @TableField("quick_show")
    private Integer quickShow;//快速展示【是否展示在介绍上；0-否 1-是】
}