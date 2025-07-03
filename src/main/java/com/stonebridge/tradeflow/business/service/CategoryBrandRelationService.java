package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.CategoryBrandRelation;
import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo.CategoryBrandRelationVO;


import java.util.List;

/**
 * 品牌分类关联
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-11-17 21:25:25
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelation> {

//    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelation categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<Brand> getBrandsByCatId(Long catId);

    List<CategoryBrandRelationVO> cateloglist(String brandId);
}

