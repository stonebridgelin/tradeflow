package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.product.SpuInfo;
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

    private SpuInfoService spuInfoService;

    @Autowired
    public SpuInfoController(SpuInfoService spuInfoService) {
        this.spuInfoService = spuInfoService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<SpuInfo> page = spuInfoService.queryPageByCondition(params);
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
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SpuSaveVo spuSaveVo) {
        spuInfoService.saveSpuInfo(spuSaveVo);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SpuInfo spuInfo) {
        spuInfoService.updateById(spuInfo);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        spuInfoService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }
}
