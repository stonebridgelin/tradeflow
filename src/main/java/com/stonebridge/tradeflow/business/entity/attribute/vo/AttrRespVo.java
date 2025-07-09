package com.stonebridge.tradeflow.business.entity.attribute.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 			"catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     */
    private String categoryName;
    private String groupName;
}
