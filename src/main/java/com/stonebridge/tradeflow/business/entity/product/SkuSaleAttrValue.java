package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * SKU销售属性值实体类
 */
@Data
@TableName("pms_sku_sale_attr_value")
public class SkuSaleAttrValue {

    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;//主键id

    @TableField(value = "sku_id", typeHandler = StringToBigIntTypeHandler.class)
    private String skuId;//sku_id

    @TableField(value = "attr_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrId;//attr_id

    @TableField("attr_name")
    private String attrName;//销售属性名

    @TableField("attr_value")
    private String attrValue;//销售属性值

    @TableField("attr_sort")
    private Integer attrSort;//顺序
}