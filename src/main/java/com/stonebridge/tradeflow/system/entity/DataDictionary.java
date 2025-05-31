package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("data_dictionary")
public class DataDictionary {
    @TableId(type = IdType.AUTO)
    private Integer id;         // 自增主键
    private String type;        // 数据类型（如 bank_account 或 platform_info）
    private String code;        // 唯一编码
    private String name;        // 名称（如平台名或账户名）
    private String comment;     // 描述信息
    private Date createTime;    // 创建时间
    private Date updateTime;    // 更新时间
}