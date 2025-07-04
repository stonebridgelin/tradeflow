package com.stonebridge.tradeflow.business.controller;

import java.util.List;

import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.CategoryBrandRelation;
import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo.CategoryBrandRelationVO;
import com.stonebridge.tradeflow.business.service.CategoryBrandRelationService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/categoryBrandRelation")
public class CategoryBrandRelationController {

    private final CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    public CategoryBrandRelationController(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    /**
     * 获取当前品牌关联的所有分类列表
     *
     * @param brandId 品牌ID
     * @return :查询结果
     */
    @GetMapping("list/{brandId}")
    //@PreAuthorize(value = "product:categorybrandrelation:list")
    public Result<Object> cateloglist(@PathVariable("brandId") String brandId) {
        List<CategoryBrandRelationVO> data = categoryBrandRelationService.cateloglist(brandId);
        return Result.ok(data);
    }


    /**
     * 保存CategoryBrandRelation对象
     *
     * @param categoryBrandRelation :只包含brandId和categoryId的categoryBrandRelation对象
     * @return :保存结果
     */
    @RequestMapping("save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public Result<Object> save(@RequestBody CategoryBrandRelation categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return Result.ok();
    }

    /**
     * 根据CategoryBrandRelation.id删除对应的数据
     *
     * @param id :要删除CategoryBrandRelation.id
     * @return :删除的结果
     */
    @RequestMapping("delete/{id}")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public Result<Object> delete(@PathVariable(value = "id") String id) {
        categoryBrandRelationService.removeById(id);
        return Result.ok();
    }

}
