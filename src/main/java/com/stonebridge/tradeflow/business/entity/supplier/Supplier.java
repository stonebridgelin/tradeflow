package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.LongToStringTypeHandler;
import lombok.Data;

import java.util.Date;

/**
 * 供应商实体类
 */
@Data
@TableName("pms_supplier")
public class Supplier {
    /**
     * 供应商ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(typeHandler = LongToStringTypeHandler.class)
    private String id;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 供应商类型
     */
    private String supplierType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}