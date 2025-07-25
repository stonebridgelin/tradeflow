package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SPU信息实体类
 */
@Data
@TableName("pms_spu_info")
public class SpuInfo {
    @TableField(value = "id", typeHandler = StringToBigIntTypeHandler.class)
    private String id;//商品id
    @TableField("spu_name")
    private String spuName;//商品名称

    @TableField("spu_description")
    private String spuDescription;//商品描述

    @TableField(value = "category_id", typeHandler = StringToBigIntTypeHandler.class)
    private String categoryId;//所属分类id

    @TableField(value = "brand_id", typeHandler = StringToBigIntTypeHandler.class)
    private String brandId;//品牌id

    @TableField("weight")
    private BigDecimal weight;//重量

    @TableField("publish_status")
    private Integer publishStatus;//上架状态[0-下架，1-上架]

    @TableField("create_time")
    private Date createTime;//创建时间

    @TableField("update_time")
    private Date updateTime;//更新时间
}