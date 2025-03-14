package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.Product;
import com.stonebridge.tradeflow.business.service.ProductService;
import com.stonebridge.tradeflow.common.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "产品管理", description = "管理产品的增删改查操作")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "获取产品详情", description = "根据产品 ID 获取产品信息，使用 Redis 缓存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回产品信息"),
            @ApiResponse(responseCode = "404", description = "产品不存在")
    })
    @GetMapping("/{id}")
    public Result<Product> getProductById(
            @Parameter(description = "产品 ID", required = true, example = "1") @PathVariable Integer id) {
        Product product = productService.getByIdWithCache(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("Product not found");
    }

    @Operation(summary = "获取所有产品", description = "返回产品列表")
    @ApiResponse(responseCode = "200", description = "成功返回产品列表")
    @GetMapping
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.list();
        return Result.success(products);
    }

    @Operation(summary = "创建产品", description = "添加一个新产品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "产品创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping
    public Result<Product> createProduct(
            @Parameter(description = "产品信息", required = true) @RequestBody Product product) {
        boolean saved = productService.save(product);
        if (saved) {
            return Result.success(product);
        }
        return Result.error("Failed to create product");
    }

    @Operation(summary = "更新产品", description = "根据产品 ID 更新产品信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "产品更新成功"),
            @ApiResponse(responseCode = "404", description = "产品不存在")
    })
    @PutMapping("/{id}")
    public Result<Product> updateProduct(
            @Parameter(description = "产品 ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "更新后的产品信息", required = true) @RequestBody Product product) {
        product.setProductId(id);
        boolean updated = productService.updateById(product);
        if (updated) {
            productService.getByIdWithCache(id); // 更新缓存
            return Result.success(product);
        }
        return Result.error("Product not found or update failed");
    }

    @Operation(summary = "删除产品", description = "根据产品 ID 删除产品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "产品删除成功"),
            @ApiResponse(responseCode = "404", description = "产品不存在")
    })
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(
            @Parameter(description = "产品 ID", required = true, example = "1") @PathVariable Integer id) {
        boolean deleted = productService.removeById(id);
        if (deleted) {
            return Result.success("Product deleted");
        }
        return Result.error("Product not found");
    }
}