package com.stonebridge.tradeflow.business.entity.attribute.vo;

import lombok.Data;

/**
 * 封装要返回给前端的Attr的分页参数=Attr+categoryName
 */
@Data
public class AttrPageVO {
    private String attrId;//属性id
    private String attrName;//属性名
    private Integer searchType;//是否需要检索[0-不需要，1-需要]
    private Integer valueType;//值类型[0-单选，1-多选]
    private String icon;//属性图标
    private String valueSelect;//可选值列表[用逗号分隔]
    private Integer attrType;//属性类型[0-销售属性，1-基本属性]
    private Integer enable;//启用状态[0-禁用，1-启用]
    private String categoryId;//所属分类id
    private String categoryName;//分类名称
    private Integer showDesc;//快速展示【是否展示在介绍上；0-否 1-是】
} 