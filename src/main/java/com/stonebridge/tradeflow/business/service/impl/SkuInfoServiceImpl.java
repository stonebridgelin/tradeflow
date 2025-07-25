package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SkuInfo;
import com.stonebridge.tradeflow.business.mapper.SkuInfoMapper;
import com.stonebridge.tradeflow.business.service.SkuInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {


    @Override
    public Page<SkuInfo> queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfoEntity) {

    }

    @Override
    public Page<SkuInfo> queryPageByCondition(Map<String, Object> params) {
        return null;
    }
}