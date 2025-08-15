package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("wms_purchase_detail")
public class PurchaseDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 采购明细主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;  // 改为Long类型，与数据库bigint对应

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
     * 采购明细状态
     * 可选值: NEW(新建), ASSIGNED(已分配), PURCHASING(正在采购),
     *        COMPLETED(已完成), FAILED(采购失败)
     */
    private String status;

    /**
     * 币种代码 (ISO 4217)
     * 例如: USD, EUR, CNY
     */
    private String currency;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}