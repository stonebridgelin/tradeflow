package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * SPU图片实体类
 */
@Data
@TableName("pms_spu_images")
public class SpuImages {

    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;//主键id

    @TableField(value = "spu_id", typeHandler = StringToBigIntTypeHandler.class)
    private String spuId;//spu_id

    @TableField("img_name")
    private String imgName;//图片名

    @TableField("img_url")
    private String imgUrl;//图片地址

    @TableField("img_sort")
    private Integer imgSort;//顺序

    @TableField("default_img")
    private Integer defaultImg;//是否默认图[0-否，1-是]
}