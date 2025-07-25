package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SkuInfo;

import java.util.Map;

/**
 * sku信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:58
 */
public interface SkuInfoService extends IService<SkuInfo> {

    Page<SkuInfo> queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfo skuInfoEntity);

    Page<SkuInfo> queryPageByCondition(Map<String, Object> params);
}

