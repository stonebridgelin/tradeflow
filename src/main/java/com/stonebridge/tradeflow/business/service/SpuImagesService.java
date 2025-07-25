package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.SpuImages;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:58
 */
public interface SpuImagesService extends IService<SpuImages> {

    Page<SpuImages> queryPage(Map<String, Object> params);

    void saveImages(String id, List<String> images);
}

