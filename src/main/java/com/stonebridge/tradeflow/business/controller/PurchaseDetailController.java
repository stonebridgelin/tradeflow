package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import com.stonebridge.tradeflow.business.service.PurchaseDetailService;
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
@RequestMapping("mallware/purchasedetail")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<PurchaseDetail> page = purchaseDetailService.queryPage(params);
        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        PurchaseDetail purchaseDetail = purchaseDetailService.getById(id);

        return Result.ok(purchaseDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetailService.save(purchaseDetail);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetailService.updateById(purchaseDetail);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        purchaseDetailService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
