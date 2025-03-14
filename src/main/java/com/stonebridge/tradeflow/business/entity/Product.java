package com.stonebridge.tradeflow.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("products") // 指定表名
public class Product {
    @TableId(value = "product_id", type = IdType.AUTO) // 主键，自增
    private Integer productId;

    @TableField("product_name") // 字段映射
    private String productName;

    private String brand;

    private String category;

    @TableField("purchase_channel")
    private String purchaseChannel;

    @TableField("purchase_link")
    private String purchaseLink;

    @TableField("img_url")
    private String imgUrl;

    @TableField("created_by")
    private Integer createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // 创建时自动填充
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // 创建和更新时自动填充
    private LocalDateTime updatedAt;
}