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
    /**
     * 平台信息ID
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 供应商ID
     */
    private String supplierId;

    /**
     * 平台名称
     */
    private String platformCode;

    /**
     * 平台地址
     */
    private String platformAddress;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
