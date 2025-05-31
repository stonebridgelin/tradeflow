package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 业务员实体类
 */
@Data
@TableName("pms_salesperson")
public class Salesperson {
    /**
     * 业务员ID
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 供应商ID
     */
    private String supplierId;

    /**
     * 业务员姓名
     */
    private String name;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 职位
     */
    private String position;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
