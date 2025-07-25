package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SkuSaleAttrValue;

import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:58
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {

    Page<SkuSaleAttrValue> queryPage(Map<String, Object> params);
}

