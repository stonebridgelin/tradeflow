package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.wms.WareSku;

import java.util.Map;

/**
 * 商品库存
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
public interface WareSkuService extends IService<WareSku> {

    Page<WareSku> queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

