package com.stonebridge.tradeflow.business.entity.supplier.dto;

import lombok.Data;

@Data
public class CategoryNode {
    private String id;
    private String name;
    // 如果前端可能发送 children 字段，可以选择性添加
    // private List<CategoryNode> children; // 视需求决定是否需要
}
