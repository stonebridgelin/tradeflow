package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 供应商与商品分类关联实体类
 */
@Data
@TableName("pms_supplier_category")
public class SupplierCategory {
    @TableId(type = IdType.AUTO)
    private String id;//关联ID
    private String supplierId;//供应商ID
    private String categoryId;//商品分类ID
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
}