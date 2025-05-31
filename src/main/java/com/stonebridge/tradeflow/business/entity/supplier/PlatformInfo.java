package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 平台信息实体类
 */
@Data
@TableName("pms_platform_info")
public class PlatformInfo {
    @TableId(type = IdType.AUTO)
    private String id; //平台信息ID
    private String supplierId; //供应商ID
    private String platformCode; //平台名称
    private String platformAddress; //平台地址
    private Date createTime; //创建时间
    private Date updateTime; //更新时间
}
