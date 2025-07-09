package com.stonebridge.tradeflow.business.entity.attribute.vo;

import lombok.Data;

@Data
public class AttrVo {
    /**
     * 属性id
     */
    private String attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 值类型[0-为单个值，1-可以选择多个值]
     */
    private Integer valueType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用分号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Integer enable;
    /**
     * 所属分类
     */
    private String categoryId;  // 如果是ID，建议用Long
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】
     */
    private Integer showDesc;
    /**
     * 分组ID
     */
    private Long attrGroupId;  // 如果是ID，建议用Long
}