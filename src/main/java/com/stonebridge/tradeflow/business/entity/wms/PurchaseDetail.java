package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购单明细信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-11-17 13:50:10
 */
@Data
@TableName("wms_purchase_detail")
public class PurchaseDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 采购明细主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 采购单ID（关联wms_purchase表）
     */
    private Long purchaseId;
    /**
     * 商品SKU编号
     */
    private Long skuId;
    /**
     * 采购商品数量
     */
    private Integer skuNum;
    /**
     * 商品采购单价
     */
    private BigDecimal skuPrice;
    /**
     * 目标仓库ID
     */
    private Long wareId;
    /**
     * 采购明细状态（0:新建 1:已分配 2:正在采购 3:已完成 4:采购失败）
     */
    private String status;

}
