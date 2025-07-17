package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.business.service.BrandService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Business库pms_brand表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/brand")
public class BrandController {
    private final BrandService brandService;

    private final MyRedisCache myRedisCache;

    @Autowired
    public BrandController(BrandService brandService,MyRedisCache myRedisCache) {
        this.brandService = brandService;
        this.myRedisCache = myRedisCache;
    }

    @GetMapping("list/{currentPage}/{pageSize}")
    public Result<Object> getBrandList(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, String keyword) {
        return brandService.queryBrandList(currentPage, pageSize, keyword);
    }


    /**
     * 更新品牌显示状态
     *
     * @param id     品牌ID
     * @param status 显示状态 (1: 启用, 0: 禁用)
     */
    @Operation(
            summary = "更新品牌显示状态",
            description = "根据品牌ID更新品牌的显示状态。状态值为1表示启用，0表示禁用。"
    )
    @GetMapping("updateStatus")
    public Result<Object> updateStatus(
            @Parameter(description = "要更新的品牌ID", example = "123", required = true)
            @RequestParam String id,
            @Parameter(description = "新的显示状态 (1: 启用, 0: 禁用)", example = "1", required = true)
            @RequestParam String status
    ) {
        brandService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     */
    @Operation(
            summary = "删除品牌",
            description = "根据品牌ID删除指定品牌。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "品牌删除成功"),
            @ApiResponse(responseCode = "400", description = "无效的品牌ID"),
            @ApiResponse(responseCode = "404", description = "品牌不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("delete/{id}")
    public Result<Object> delete(@Parameter(description = "品牌ID", example = "123", required = true) @PathVariable(value = "id") String id) {
        brandService.delete(id);
        return Result.ok();
    }

    @GetMapping("query/{id}")
    public Result<Object> selectById(@PathVariable(value = "id") String id) {
        return Result.ok(brandService.getById(id));
    }

    @PostMapping("createBrand")
    public Result<Object> createBrand(@RequestBody Brand brand) {
        brandService.createBrand(brand);
        return Result.ok();
    }

    @PutMapping("updateBrand")
    public Result<Object> updateBrand(@RequestBody Brand brand) {
        brandService.updateBrand(brand);
        return Result.ok();
    }


    /**
     * 根据分类的id（categoryId），先根据categoryId从pms_category_brand_relation查询对应的brand的id，再查询brand的详细信息
     */
    @GetMapping("queryBrandByCategoryId/{categoryId}")
    public Result<Object> queryBrandByCategoryId(@PathVariable("categoryId") String categoryId, String keyWord) {
        List<Brand> brandList = brandService.queryBrandByCategoryId(categoryId, keyWord);
        return Result.ok(brandList);
    }

}
