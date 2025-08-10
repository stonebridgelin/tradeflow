package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.WareOrderTask;
import com.stonebridge.tradeflow.business.service.WareOrderTaskService;
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
@RequestMapping("mallware/wareordertask")
public class WareOrderTaskController {
    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<WareOrderTask> page = wareOrderTaskService.queryPage(params);

        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        WareOrderTask wareOrderTask = wareOrderTaskService.getById(id);

        return Result.ok(wareOrderTask);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody WareOrderTask wareOrderTask) {
        wareOrderTaskService.save(wareOrderTask);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody WareOrderTask wareOrderTask) {
        wareOrderTaskService.updateById(wareOrderTask);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        wareOrderTaskService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
