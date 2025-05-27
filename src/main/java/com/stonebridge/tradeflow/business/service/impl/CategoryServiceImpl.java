package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.mapper.CategoryMapper;
import com.stonebridge.tradeflow.business.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private final CategoryMapper categoryMapper;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, @Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.categoryMapper = categoryMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> selectCategoriesByParentId(String parentId) {
        //1.根据id条件进行查询，返回结果为list集合
        //SELECT * FROM category WHERE parent_id=?
        List<Category> categoryList = categoryMapper.selectList(new QueryWrapper<Category>().eq("parent_id", parentId));
        //2.遍历返回list集合
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        } else {
            for (Category category : categoryList) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category WHERE parent_id=?", Integer.class, category.getId());
                if (count > 0) {
                    category.setHasChildren(true);
                } else {
                    category.setHasChildren(false);
                }
            }
        }
        return categoryList;
    }
}
