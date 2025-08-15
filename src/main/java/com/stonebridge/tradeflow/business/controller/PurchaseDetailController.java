package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import com.stonebridge.tradeflow.business.entity.wms.vo.PurchaseDetailVo;
import com.stonebridge.tradeflow.business.service.PurchaseDetailService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@RestController
@RequestMapping("api/purchaseDetail")
public class PurchaseDetailController {
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    public PurchaseDetailController(PurchaseDetailService purchaseDetailService) {
        this.purchaseDetailService = purchaseDetailService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<Object> list(@RequestParam Map<String, Object> params) {
        Page<PurchaseDetailVo> page = purchaseDetailService.queryPage(params);
        return Result.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result<Object> info(@PathVariable("id") Long id) {
        PurchaseDetail purchaseDetail = purchaseDetailService.getById(id);

        return Result.ok(purchaseDetail);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result<Object> save(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetail.setCreateTime(new Date());
        purchaseDetailService.save(purchaseDetail);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result<Object> update(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetail.setUpdateTime(new Date());
        purchaseDetailService.updateById(purchaseDetail);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result<Object> delete(@RequestBody Long[] ids) {
        purchaseDetailService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }
}
