package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.category.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> listWithTree();

    void removeCategoryByIds(List<String> ids);

    void saveCategory(Category category);

    void updateCategory(Category category);
}
