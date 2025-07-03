package com.stonebridge.tradeflow.business.entity.categoryBrandRelation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * 品牌分类关联实体类
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelation {

	@TableField(value = "id", typeHandler = StringToBigIntTypeHandler.class)
	private String id;//主键ID

	@TableField(value = "brand_id", typeHandler = StringToBigIntTypeHandler.class)
	private String brandId;//品牌id

	@TableField(value = "category_id", typeHandler = StringToBigIntTypeHandler.class)
	private String categoryId;//分类id
}
