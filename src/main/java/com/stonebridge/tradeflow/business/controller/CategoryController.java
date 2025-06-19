package com.stonebridge.tradeflow.business.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.service.CategoryService;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "Business库category表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    public static final Integer CATEGORY_STATUS_ACTIVE = 1;

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
    public Result<Object> removeCategoryByIds(@RequestBody List<String> ids) {
        categoryService.removeCategoryByIds(ids);
        return Result.ok();
    }

    /**
     * 新增Category对象
     *
     * @param category 新增的Category数据
     * @return ：新增的Category
     */
    @PutMapping("add")
    public Result<Category> appendCategory(@RequestBody Category category) {
        category.setStatus(CATEGORY_STATUS_ACTIVE);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        categoryService.save(category);
        return Result.ok(category);
    }

    @GetMapping("query/{id}")
    public Result<ObjectNode> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getById(id);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.createObjectNode();
        obj.put("id", category.getId());
        obj.put("name", category.getName());
        String parentId = category.getParentId();
        obj.put("parentId", parentId);
        String parentName = "当前节点为1级节点";
        if (category.getLevel() != 1) {
            Category parentCategory = categoryService.getById(parentId);
            parentName = parentCategory.getName();
        }
        obj.put("parentName", parentName);
        return Result.ok(obj);
    }

    @PutMapping("update")
    public Result<Category> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok(category);
    }
}
