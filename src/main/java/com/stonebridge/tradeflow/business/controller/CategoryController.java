package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.service.CategoryService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{parentId}")
    public Result<List<Category>> getCategories(@PathVariable(value = "parentId") String parentId) {
        List<Category> categories = categoryService.selectCategoriesByParentId(parentId);
        return Result.ok(categories);
    }
}
