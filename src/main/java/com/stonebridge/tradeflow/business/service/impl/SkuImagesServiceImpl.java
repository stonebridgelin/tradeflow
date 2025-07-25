package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SkuImages;
import com.stonebridge.tradeflow.business.mapper.SkuImagesMapper;
import com.stonebridge.tradeflow.business.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesMapper, SkuImages> implements SkuImagesService {


    @Override
    public Page<SkuImages> queryPage(Map<String, Object> params) {
        return null;
    }
}