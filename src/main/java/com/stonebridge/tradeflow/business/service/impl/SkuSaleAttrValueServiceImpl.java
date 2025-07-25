package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SkuSaleAttrValue;
import com.stonebridge.tradeflow.business.mapper.SkuSaleAttrValueMapper;
import com.stonebridge.tradeflow.business.service.SkuSaleAttrValueService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueMapper, SkuSaleAttrValue> implements SkuSaleAttrValueService {


    @Override
    public Page<SkuSaleAttrValue> queryPage(Map<String, Object> params) {
        return null;
    }
}