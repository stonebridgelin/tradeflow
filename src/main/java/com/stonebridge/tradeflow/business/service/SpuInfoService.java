package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SpuInfo;
import com.stonebridge.tradeflow.business.entity.spu.SpuInfoVo;
import com.stonebridge.tradeflow.business.entity.spu.SpuSaveVo;

import java.util.Map;

public interface SpuInfoService extends IService<SpuInfo> {

    Page<SpuInfo> queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void saveBaseSpuInfo(SpuInfo spuInfo);

    Page<SpuInfoVo> queryPageByCondition(Map<String, Object> params);
}
