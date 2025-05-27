package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> selectCategoriesByParentId(String parentId);
}
