package com.stonebridge.tradeflow.business.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * SPU信息详情实体类
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDesc {
    @TableField(value = "spu_id", typeHandler = StringToBigIntTypeHandler.class)
    private String spuId;//商品id
    @TableField("description")
    private String description;//商品介绍
}