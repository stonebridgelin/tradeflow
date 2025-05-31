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
    @TableId(type = IdType.AUTO)
    private String id;  //业务员ID
    private String supplierId; //供应商ID
    private String name; //业务员姓名
    private String contactPhone; //联系电话
    private String email; //电子邮件
    private String position;//职位
    private String wechat;//微信号
    private Date createTime; //创建时间
    private Date updateTime;//更新时间
}
