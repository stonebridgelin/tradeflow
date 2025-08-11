package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.WareSku;
import com.stonebridge.tradeflow.business.service.WareSkuService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品库存
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
@RestController
@RequestMapping("api/wareSku")
public class WareSkuController {
    private final WareSkuService wareSkuService;

    @Autowired
    public WareSkuController(WareSkuService wareSkuService) {
        this.wareSkuService = wareSkuService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<WareSku> page = wareSkuService.queryPage(params);
        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result<Object> info(@PathVariable("id") Long id) {
        WareSku wareSku = wareSkuService.getById(id);

        return Result.ok(wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result<Object> save(@RequestBody WareSku wareSku) {
        wareSkuService.save(wareSku);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result<Object> update(@RequestBody WareSku wareSku) {
        wareSkuService.updateById(wareSku);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result<Object> delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }

}
