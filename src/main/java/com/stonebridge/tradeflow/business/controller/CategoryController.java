package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.service.CategoryService;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Business库category表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("listWithTree")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.listWithTree();
        return Result.ok(categories);
    }

    /**
     * 根据多个Categories的id保存到List中，进行批量删除
     *
     * @param ids 即将被删除的Categories的id集合
     * @return 处理结构
     */
    @PostMapping("delete")
    public Result removeCategoryByIds(@RequestBody List<String> ids) {
        categoryService.removeCategoryByIds(ids);
        return Result.ok();
    }
}
