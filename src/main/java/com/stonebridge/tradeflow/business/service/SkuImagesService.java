package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SkuImages;

import java.util.Map;

public interface SkuImagesService extends IService<SkuImages> {

    Page<SkuImages> queryPage(Map<String, Object> params);
}
