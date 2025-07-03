package com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

@Data
public class CategoryBrandRelationVO {
    private String id;//主键ID

    private String brandId;//品牌id

    private String categoryId;//分类id

    private String brandName;//品牌名

    private String categoryName;//分类名
}
