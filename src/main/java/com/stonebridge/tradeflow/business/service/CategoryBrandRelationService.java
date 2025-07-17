package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.CategoryBrandRelation;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo.CategoryBrandRelationVO;

import java.util.List;

/**
 * 品牌分类关联
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelation> {

    void saveDetail(CategoryBrandRelation categoryBrandRelation);

    List<CategoryBrandRelationVO> cateloglist(String brandId);

    List<String> queryBrandIdsByCategoryId(String categoryId);
}

