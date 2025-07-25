package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.product.*;
import com.stonebridge.tradeflow.business.entity.spu.BaseAttrs;
import com.stonebridge.tradeflow.business.entity.spu.Images;
import com.stonebridge.tradeflow.business.entity.spu.Skus;
import com.stonebridge.tradeflow.business.entity.spu.SpuSaveVo;
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
        //1.保存spu的基本信息 pms_spu_info
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfo);

        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decripts = spuSaveVo.getDecript();
        SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDescription(String.join(",", decripts));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfo.getId(), images);

        //4.保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValue> list = baseAttrs.stream().map(item -> {
            ProductAttrValue valueEntity = new ProductAttrValue();
            valueEntity.setAttrId(String.valueOf(item.getAttrId()));
            valueEntity.setAttrName(attrService.getById(item.getAttrId()).getAttrName());
            valueEntity.setAttrValue(item.getAttrValues());
            valueEntity.setQuickShow(String.valueOf(item.getShowDesc()));
            valueEntity.setSpuId(spuInfo.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        valueService.saveProductAttr(list);

        //5.保存spu的积分信息；gulimall_product通过feign调用sms_spu_bounds完成保存
//        Bounds bounds = spuSaveVo.getBounds();
//        SpuBoundTo spuBoundTo = new SpuBoundTo();
//        BeanUtils.copyProperties(bounds, spuBoundTo);
//        spuBoundTo.setSpuId(spuInfo.getId());
//        Result result = couponFeignService.saveSpuBounds(spuBoundTo);
//        if (result.getCode() != 0) {
//            log.error("远程保存spu积分信息失败");
//        }

        //6.保存当前SPU对应的SKU信息
        List<Skus> skusList = spuSaveVo.getSkus();
        if (!skusList.isEmpty()) {
            skusList.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //skuName、price、skuTitle、skuSubtitle
                SkuInfo skuInfoEntity = new SkuInfo();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setCategoryId(spuInfo.getCategoryId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfo.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                //6.1.sku的基本信息pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                String skuId = skuInfoEntity.getSkuId();
                List<SkuImages> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImages skuImagesEntity = new SkuImages();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtil.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                //6.2.sku的图片信息pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的无需保存
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValue> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValue attrValueEntity = new SkuSaleAttrValue();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                //6.3.sku的销售属性信息pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

//                //6.4.sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder(sku打折表)\sms_sku_full_reduction(满减表)\sms_member_price(会员价格表)
//                SkuReductionTo skuReductionTo = new SkuReductionTo();
//                BeanUtils.copyProperties(item, skuReductionTo);
//                skuReductionTo.setSkuId(skuId);
//                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
//                    Result result1 = couponFeignService.saveSkuReduction(skuReductionTo);
//                    if (result1.getCode() != 0) {
//                        log.error("远程保存sku优惠信息失败");
//                    }
//                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfo spuInfo) {

    }

    @Override
    public Page<SpuInfo> queryPageByCondition(Map<String, Object> params) {
        return null;
    }
}