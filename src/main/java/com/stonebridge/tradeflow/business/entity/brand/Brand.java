package com.stonebridge.tradeflow.business.entity.brand;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 品牌实体类，对应数据库表 pms_brand
 *
 * @author
 * @since 2025-06-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_brand")
public class Brand implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private String id; //品牌id
    private String name;//品牌名
    private String logo;//品牌logo地址
    private String description;//介绍
    private Integer showStatus;//显示状态[0-不显示；1-显示]
    private String firstLetter;//检索首字母
    private Integer sort;//排序
    @TableField("deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}