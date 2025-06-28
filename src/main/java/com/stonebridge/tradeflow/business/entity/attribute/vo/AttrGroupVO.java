package com.stonebridge.tradeflow.business.entity.attribute.vo;

import lombok.Data;

/**
 * 封装要返回给前端的AttrGroup的参数=AttrGroup+categoryName
 */
@Data
public class AttrGroupVO {
    private String attrGroupId;//分组id
    private String attrGroupName;//组名
    private String sort;//排序
    private String description;//描述
    private String icon;//组图标
    private String categoryId;//所属分类id
    private String categoryName;
}
