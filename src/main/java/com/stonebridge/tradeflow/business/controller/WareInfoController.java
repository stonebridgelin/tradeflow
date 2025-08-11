package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.wms.WareInfo;
import com.stonebridge.tradeflow.business.service.WareInfoService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 仓库信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@RestController
@RequestMapping("api/wareInfo")
public class WareInfoController {
    private WareInfoService wareInfoService;

    @Autowired
    public WareInfoController(WareInfoService wareInfoService) {
        this.wareInfoService = wareInfoService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(String keyWord) {
        List<WareInfo> list = wareInfoService.querylist(keyWord);
        return Result.ok(list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result<Object> info(@PathVariable("id") Long id) {
        WareInfo wareInfo = wareInfoService.getById(id);
        return Result.ok(wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result<Object> save(@RequestBody WareInfo wareInfo) {
        wareInfoService.save(wareInfo);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result<Object> update(@RequestBody WareInfo wareInfo) {
        wareInfoService.updateById(wareInfo);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result<Object> delete(@RequestBody Long[] ids) {
        wareInfoService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }

}
