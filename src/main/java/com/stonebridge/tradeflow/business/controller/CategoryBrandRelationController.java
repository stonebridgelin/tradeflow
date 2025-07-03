package com.stonebridge.tradeflow.business.controller;

import java.util.Arrays;
import java.util.List;

import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.CategoryBrandRelation;
import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.business.entity.categoryBrandRelation.vo.CategoryBrandRelationVO;
import com.stonebridge.tradeflow.business.service.CategoryBrandRelationService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product/categorybrandrelation")
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
    @GetMapping("/catelog/list")
    //@PreAuthorize(value = "product:categorybrandrelation:list")
    public Result<Object> cateloglist(@RequestParam("brandId") String brandId) {
        List<CategoryBrandRelationVO> data = categoryBrandRelationService.cateloglist(brandId);
        return Result.ok(data);
    }


    /**
     * 保存CategoryBrandRelation对象
     *
     * @param categoryBrandRelation :只包含brandId和categoryId的categoryBrandRelation对象
     * @return :保存结果
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public Result<Object> save(@RequestBody CategoryBrandRelation categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return Result.ok();
    }

    /**
     * 根据CategoryBrandRelation.id删除对应的数据
     *
     * @param ids :要删除CategoryBrandRelation.id集合
     * @return :删除的结果
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public Result<Object> delete(@RequestBody String[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }


    /**
     *  1、Controller：处理请求，接受和校验数据
     *  2、Service接受controller传来的数据，进行业务处理
     *  3、Controller接受Service处理完的数据，封装页面指定的vo
     * @param catId
     * @return :
     */
    @GetMapping("/brands/list")
    public Result<Object> relationBrandsList(@RequestParam(value = "catId",required = true)Long catId){
        List<Brand> vos = categoryBrandRelationService.getBrandsByCatId(catId);
        return Result.ok(vos);

    }




//
//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    //@RequiresPermissions("product:categorybrandrelation:list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = categoryBrandRelationService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{id}")
//    //@RequiresPermissions("product:categorybrandrelation:info")
//    public R info(@PathVariable("id") Long id){
//		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);
//
//        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    //@RequiresPermissions("product:categorybrandrelation:save")
//    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
//
//
//		categoryBrandRelationService.saveDetail(categoryBrandRelation);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    //@RequiresPermissions("product:categorybrandrelation:update")
//    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
//		categoryBrandRelationService.updateById(categoryBrandRelation);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    //@RequiresPermissions("product:categorybrandrelation:delete")
//    public R delete(@RequestBody Long[] ids){
//		categoryBrandRelationService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
