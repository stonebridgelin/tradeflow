package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.UndoLog;
import com.stonebridge.tradeflow.business.service.UndoLogService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@RestController
@RequestMapping("mallware/undolog")
public class UndoLogController {
    @Autowired
    private UndoLogService undoLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<UndoLog> page = undoLogService.queryPage(params);

        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        UndoLog undoLog = undoLogService.getById(id);

        return Result.ok(undoLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody UndoLog undoLog) {
        undoLogService.save(undoLog);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody UndoLog undoLog) {
        undoLogService.updateById(undoLog);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        undoLogService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
