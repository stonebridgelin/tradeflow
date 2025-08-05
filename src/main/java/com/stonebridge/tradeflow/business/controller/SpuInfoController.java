package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.product.SpuInfo;
import com.stonebridge.tradeflow.business.entity.spu.SpuInfoVo;
import com.stonebridge.tradeflow.business.entity.spu.SpuSaveVo;
import com.stonebridge.tradeflow.business.service.SpuInfoService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * spu信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:30
 */
@RestController
@RequestMapping("api/spuInfo")
public class SpuInfoController {

    private final SpuInfoService spuInfoService;

    @Autowired
    public SpuInfoController(SpuInfoService spuInfoService) {
        this.spuInfoService = spuInfoService;
    }

    /**
     * 使用页面src/pages/product/SpuAdd.vue --> submitSpuData -->/api/spuInfo/save
     * 保存spu以及对应的多个sku的详细信息
     *
     * @param spuSaveVo ：封装接收的多个sku的详细信息
     * @return ：保存成功的状态
     */
    @RequestMapping("/save")
    public Result<Object> save(@RequestBody SpuSaveVo spuSaveVo) {
        spuInfoService.saveSpuInfo(spuSaveVo);
        return Result.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{currentPage}/{pageSize}")
    public Result<Object> list(@PathVariable("currentPage") String currentPage, @PathVariable("pageSize") String pageSize, @RequestParam Map<String, Object> params) {
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        Page<SpuInfoVo> page = spuInfoService.queryPageByCondition(params);
        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        SpuInfo spuInfo = spuInfoService.getById(id);
        return Result.ok(spuInfo);
    }


    /**
     * 修改
     */
    @PostMapping("/updateStatus")
    public Result<Object> update(@RequestBody SpuInfo spuInfo) {
        spuInfoService.updateById(spuInfo);
        return Result.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String spuId) {
        spuInfoService.deleteSpuById(spuId);
        return Result.ok();
    }
}
