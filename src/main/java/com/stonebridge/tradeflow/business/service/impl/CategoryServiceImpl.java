package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.mapper.CategoryMapper;
import com.stonebridge.tradeflow.business.service.CategoryService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.stonebridge.tradeflow.business.controller.CategoryController.CATEGORY_STATUS_ACTIVE;

/**
 * 分类服务实现类，用于管理分类相关的业务逻辑。
 * 继承 MyBatis Plus 的 ServiceImpl 以复用其内置的 CRUD 操作。
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 根分类的父 ID 常量
     */
    private static final String ROOT_PARENT_ID = "0";

    private final CategoryMapper categoryMapper;

    private final MyRedisCache myRedisCache;

    /**
     * 构造函数，通过依赖注入初始化 CategoryMapper。
     *
     * @param categoryMapper 用于操作 Category 实体的数据库映射器
     * @throws NullPointerException 如果 categoryMapper 为 null
     */
    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, MyRedisCache myRedisCache) {
        this.categoryMapper = Objects.requireNonNull(categoryMapper, "CategoryMapper 不能为空");
        this.myRedisCache = myRedisCache;
    }

    /**
     * 获取所有分类数据并组织成树形结构。
     * 实现步骤：
     * 1. 从 pms_category 表查询所有分类数据
     * 2. 筛选出父 ID 为 "0" 的一级分类
     * 3. 通过递归方法为每个分类设置其子分类
     * 4. 根据 orderNum 对分类进行排序
     *
     * @return 包含所有一级分类的列表，每个分类包含其子分类，形成树形结构
     */
    @Override
    public List<Category> listWithTree() {
        // 查询所有分类数据
        List<Category> allCategories = categoryMapper.selectList(null);

        // 如果分类数据为空或 null，返回空列表以避免空指针异常
        if (allCategories == null || allCategories.isEmpty()) {
            return Collections.emptyList();
        }

        // 筛选一级分类，设置子分类，并按 orderNum 排序
        return allCategories.stream()
                .filter(category -> ROOT_PARENT_ID.equals(category.getParentId()))
                .peek(category -> {
                    category.setChildren(getChildrenCategories(category.getId(), allCategories));
                    if (!category.getChildren().isEmpty()) {
                        category.setHasChildren(true);
                    }
                })
                .sorted(Comparator.comparingInt(category ->
                        category.getOrderNum() == null ? 0 : category.getOrderNum()))
                .collect(Collectors.toList());
    }

    /**
     * 删除指定id的category
     * @param ids :category的id集合
     */
    @Override
    public void removeCategoryByIds(List<String> ids) {
        categoryMapper.deleteBatchIds(ids);
        this.refreshCategoryCache();
    }

    /**
     * 保存Category对象
     * @param category ：Category对象
     */
    @Override
    public void saveCategory(Category category) {
        category.setStatus(CATEGORY_STATUS_ACTIVE);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        this.save(category);
        this.refreshCategoryCache();
    }

    /**
     * 更新Category对象
     * @param category ：要更新的Category对象
     */
    @Override
    public void updateCategory(Category category) {
        this.updateById(category);
        this.refreshCategoryCache();
    }

    /**
     * 递归获取指定分类的子分类列表。
     * 通过父分类 ID 查找子分类，并为每个子分类递归设置其子分类。
     *
     * @param parentId      父分类的 ID
     * @param allCategories 所有分类的列表
     * @return 子分类列表，按 orderNum 排序
     */
    private List<Category> getChildrenCategories(String parentId, List<Category> allCategories) {
        // 筛选出父 ID 等于 parentId 的子分类，设置其子分类并排序
        return allCategories.stream()
                .filter(category -> parentId.equals(category.getParentId()))
                .peek(category -> {
                    category.setChildren(getChildrenCategories(category.getId(), allCategories));
                    if (!category.getChildren().isEmpty()) {
                        category.setHasChildren(true);
                    }
                })
                .sorted(Comparator.comparingInt(category -> category.getOrderNum() == null ? 0 : category.getOrderNum()))
                .collect(Collectors.toList());
    }

    /**
     * 刷新Category在redis的缓存
     */
    public void refreshCategoryCache() {
        myRedisCache.refreshCache(MyRedisCache.CacheConstants.TYPE_CATEGORY);
    }
}