package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * SKU图片实体类
 */
@Data
@TableName("pms_sku_images")
public class SkuImages {

    @TableField(value = "id", typeHandler = StringToBigIntTypeHandler.class)
    private String id;//id

    @TableField(value = "sku_id", typeHandler = StringToBigIntTypeHandler.class)
    private String skuId;//sku_id

    @TableField("img_url")
    private String imgUrl;//图片地址

    @TableField("img_sort")
    private Integer imgSort;//排序

    @TableField("default_img")
    private Integer defaultImg;//默认图[0-不是默认图，1-是默认图]
}