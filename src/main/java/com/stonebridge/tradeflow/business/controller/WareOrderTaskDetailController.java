package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.WareOrderTaskDetail;
import com.stonebridge.tradeflow.business.service.WareOrderTaskDetailService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 库存工作单
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
@RestController
@RequestMapping("mallware/wareordertaskdetail")
public class WareOrderTaskDetailController {
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        Page<WareOrderTaskDetail> page = wareOrderTaskDetailService.queryPage(params);

        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        WareOrderTaskDetail wareOrderTaskDetail = wareOrderTaskDetailService.getById(id);

        return Result.ok(wareOrderTaskDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody WareOrderTaskDetail wareOrderTaskDetail) {
        wareOrderTaskDetailService.save(wareOrderTaskDetail);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody WareOrderTaskDetail wareOrderTaskDetail) {
        wareOrderTaskDetailService.updateById(wareOrderTaskDetail);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        wareOrderTaskDetailService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }

}
