package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SKU信息实体类
 */
@Data
@TableName("pms_sku_info")
public class SkuInfo {
    @TableField(value = "sku_id", typeHandler = StringToBigIntTypeHandler.class)
    private String skuId;//skuId

    @TableField(value = "spu_id", typeHandler = StringToBigIntTypeHandler.class)
    private String spuId;//spuId

    @TableField("sku_name")
    private String skuName;// sku名称

    @TableField("sku_desc")
    private String skuDesc;//sku介绍描述

    @TableField(value = "catalog_id", typeHandler = StringToBigIntTypeHandler.class)
    private String catalogId;//所属分类id

    @TableField(value = "brand_id", typeHandler = StringToBigIntTypeHandler.class)
    private String brandId;//品牌id

    @TableField("sku_default_img")
    private String skuDefaultImg;//默认图片

    @TableField("sku_title")
    private String skuTitle;//标题

    @TableField("sku_subtitle")
    private String skuSubtitle;//副标题

    @TableField("price")
    private BigDecimal price;//价格

    @TableField("sale_count")
    private Long saleCount;// 销量
}