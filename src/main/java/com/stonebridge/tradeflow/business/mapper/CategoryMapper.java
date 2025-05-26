package com.stonebridge.tradeflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.business.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper 接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}