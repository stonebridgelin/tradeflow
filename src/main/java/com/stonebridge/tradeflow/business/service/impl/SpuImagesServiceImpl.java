package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SpuImages;
import com.stonebridge.tradeflow.business.mapper.SpuImagesMapper;
import com.stonebridge.tradeflow.business.service.SpuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesMapper, SpuImages> implements SpuImagesService {

    @Override
    public Page<SpuImages> queryPage(Map<String, Object> params) {
       return null;
    }

    /**
     * 保存spu的图片集 pms_spu_images
     *
     * @param id
     * @param images
     */
    @Override
    public void saveImages(String id, List<String> images) {
        if (!images.isEmpty()) {
            List<SpuImages> list = images.stream().map(img -> {
                SpuImages spuImages = new SpuImages();
                spuImages.setSpuId(id);
                spuImages.setImgUrl(img);
                return spuImages;
            }).collect(Collectors.toList());
            this.saveBatch(list);
        }
    }

}