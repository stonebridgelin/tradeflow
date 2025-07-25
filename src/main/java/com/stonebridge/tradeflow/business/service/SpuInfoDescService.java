package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SpuInfoDesc;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:58
 */
public interface SpuInfoDescService extends IService<SpuInfoDesc> {

    Page<SpuInfoDesc> queryPage(Map<String, Object> params);

    void saveSpuInfoDesc(SpuInfoDesc descEntity);
}

