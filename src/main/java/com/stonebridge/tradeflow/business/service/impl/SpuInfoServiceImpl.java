package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stonebridge.tradeflow.business.entity.product.*;
import com.stonebridge.tradeflow.business.entity.spu.*;
import com.stonebridge.tradeflow.business.mapper.SpuInfoMapper;
import com.stonebridge.tradeflow.business.service.*;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService {

    private final SpuInfoDescService spuInfoDescService;

    private final SpuImagesService spuImagesService;

    private final AttrService attrService;

    private final ProductAttrValueService valueService;

    private final SkuInfoService skuInfoService;

    private final SkuImagesService skuImagesService;

    private final SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService, SpuImagesService spuImagesService, AttrService attrService, ProductAttrValueService valueService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.valueService = valueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
    }

    @Override
    public Page<SpuInfo> queryPage(Map<String, Object> params) {
//        Page<SpuInfo> page = this.page(new Query<SpuInfo>().getPage(params), new QueryWrapper<>());
//        return page;
        return null;
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        Date now = new Date();
        //1.保存spu的基本信息到表pms_spu_info
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        spuInfo.setCreateTime(now);
        spuInfo.setUpdateTime(now);
        this.saveBaseSpuInfo(spuInfo);
        String spuId = spuInfo.getId();

        //2.保存spu的描述图片到表pms_spu_info_desc
        List<String> decripts = spuSaveVo.getDecript();
        SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
        spuInfoDesc.setSpuId(spuId);
        spuInfoDesc.setDescription(String.join(",", decripts));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuId, images);

        //4.保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValue> productAttrValues = baseAttrs.stream().map(item -> {
            ProductAttrValue productAttrValue = new ProductAttrValue();
            productAttrValue.setAttrId(String.valueOf(item.getAttrId()));
            productAttrValue.setAttrName(attrService.getById(item.getAttrId()).getAttrName());
            productAttrValue.setAttrValue(item.getAttrValues());
            productAttrValue.setQuickShow(String.valueOf(item.getShowDesc()));
            productAttrValue.setSpuId(spuId);
            return productAttrValue;
        }).collect(Collectors.toList());
        valueService.saveProductAttr(productAttrValues);


        //5.保存当前SPU对应的所有SKU信息
        List<Skus> skusList = spuSaveVo.getSkus();
        if (!skusList.isEmpty()) {
            for (Skus sku : skusList) {
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //5.1.sku的基本信息保存到pms_sku_info
                // 从com.stonebridge.tradeflow.business.entity.spu.Skus拷贝数据到com.stonebridge.tradeflow.business.entity.product.SkuInfo
                // 包括：spuId、skuName、price、skuTitle、skuSubtitle、defaultImg；（brandId、cayegoryId、SpuId）(来自 spuInfo)
                SkuInfo skuInfo = new SkuInfo();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setBrandId(spuInfo.getBrandId());
                skuInfo.setCategoryId(spuInfo.getCategoryId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSpuId(spuId);
                skuInfo.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfo);
                String skuId = skuInfo.getSkuId();

                //5.2.保存每个sku对应的图片到pms_sku_images表
                List<SkuImages> imagesEntities = sku.getImages().stream().map(img -> {
                    SkuImages skuImage = new SkuImages();
                    skuImage.setSkuId(skuId);
                    skuImage.setImgUrl(img.getImgUrl());
                    skuImage.setDefaultImg(img.getDefaultImg());
                    return skuImage;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtil.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                //5.3.sku的销售属性信息保存到pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValue> skuSaleAttrValues = attrs.stream().map(attr -> {
                    SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                    BeanUtils.copyProperties(attr, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuId);
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValues);
            }
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfo spuInfo) {
        this.baseMapper.insert(spuInfo);
    }

    @Override
    public Page<SpuInfo> queryPageByCondition(Map<String, Object> params) {
        return null;
    }
}