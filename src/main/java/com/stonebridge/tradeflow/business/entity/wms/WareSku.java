package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Data
@TableName("wms_ware_sku")
public class WareSku implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 库存记录主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 商品SKU编号
     */
    private Long skuId;
    /**
     * 存储仓库ID
     */
    private Long wareId;
    /**
     * 当前库存数量
     */
    private Integer stock;
    /**
     * 商品SKU名称
     */
    private String skuName;
    /**
     * 被锁定的库存数量（预占用）
     */
    private Integer stockLocked;

}
