package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.product.SkuInfo;
import com.stonebridge.tradeflow.business.service.SkuInfoService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("api/skuInfo")
public class SkuInfoController {

    private final SkuInfoService skuInfoService;

    @Autowired
    public SkuInfoController(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    /**
     * 列表
     */
    @GetMapping("list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<SkuInfo> page = skuInfoService.queryPageByCondition(params);
        return Result.ok(page);
    }
}
