package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.CategoryBrandRelation;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo.CategoryBrandRelationVO;
import com.stonebridge.tradeflow.business.mapper.CategoryBrandRelationMapper;
import com.stonebridge.tradeflow.business.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationMapper, CategoryBrandRelation> implements CategoryBrandRelationService {

    private final MyRedisCache myRedisCache;

    @Autowired
    public CategoryBrandRelationServiceImpl(MyRedisCache myRedisCache) {
        this.myRedisCache = myRedisCache;
    }

    @Override
    public void saveDetail(CategoryBrandRelation categoryBrandRelation) {
        this.save(categoryBrandRelation);
    }

    @Override
    public List<CategoryBrandRelationVO> cateloglist(String brandId) {
        List<CategoryBrandRelation> list = this.list(new QueryWrapper<CategoryBrandRelation>().eq("brand_id", brandId));
        List<CategoryBrandRelationVO> voList = new ArrayList<>();
        CategoryBrandRelationVO vo;
        for (CategoryBrandRelation categoryBrandRelation : list) {
            vo = new CategoryBrandRelationVO();
            BeanUtils.copyProperties(categoryBrandRelation, vo);
            vo.setCategoryName(myRedisCache.getCategoryNameById(categoryBrandRelation.getCategoryId()));
            vo.setBrandName(myRedisCache.getBrandById(categoryBrandRelation.getBrandId()).getName());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 根据pms_category_brand_relation根据CategoryId查询对应的brandId
     *
     * @param categoryId ：
     * @return ：pms_category_brand_relation表关联的brandId
     */
    public List<String> queryBrandIdsByCategoryId(String categoryId) {
        List<String> brandIds = new ArrayList<>();
        QueryWrapper<CategoryBrandRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        List<CategoryBrandRelation> list = this.list(queryWrapper);
        for (CategoryBrandRelation categoryBrandRelation : list) {
            brandIds.add(categoryBrandRelation.getBrandId());
        }
        return brandIds;
    }
}