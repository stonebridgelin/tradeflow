package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 供应商主表实体类
 */
@Data
@TableName("pms_supplier")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("supplier_name_en")
    private String supplierNameEn;

    @TableField("supplier_name_cn")
    private String supplierNameCn;

    @TableField("supplier_code")
    private String supplierCode;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    private String email;

    private String address;

    @TableField("supplier_type")
    private String supplierType;

    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
