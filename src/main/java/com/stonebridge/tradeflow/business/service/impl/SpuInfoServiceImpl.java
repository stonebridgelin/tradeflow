package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stonebridge.tradeflow.business.entity.product.*;
import com.stonebridge.tradeflow.business.entity.spu.*;
import com.stonebridge.tradeflow.business.mapper.SpuInfoMapper;
import com.stonebridge.tradeflow.business.service.*;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    private final MyRedisCache  myRedisCache;

    @Autowired
    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService, SpuImagesService spuImagesService, AttrService attrService, ProductAttrValueService valueService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService, MyRedisCache myRedisCache) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.valueService = valueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.myRedisCache = myRedisCache;
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

    /**
     * 根据条件分页查询SPU信息（带扩展信息）
     * 
     * @param params 查询参数，包含以下字段：
     *               - categoryId: 分类ID（可选）
     *               - brandId: 品牌ID（可选，值为"0"时忽略）
     *               - keyword: 关键词（可选，模糊匹配SPU名称或精确匹配ID）
     *               - publishStatus: 发布状态（可选，0-下架，1-上架）
     *               - currentPage: 当前页码（必需，默认为1）
     *               - pageSize: 每页大小（必需，默认为10，最大为100）
     * @return 包含分类名称和品牌名称的SPU分页结果
     * @throws CustomizeException 当参数验证失败或查询失败时抛出
     */
    @Override
    public Page<SpuInfoVo> queryPageByCondition(Map<String, Object> params) {
        // 参数空值检查
        if (params == null) {
            params = new HashMap<>();
        }

        // 安全获取并验证分页参数
        long currentPage = 1L;
        long pageSize = 10L;
        
        try {
            // 获取当前页码
            Object currentPageObj = params.get("currentPage");
            if (currentPageObj != null) {
                if (currentPageObj instanceof Number) {
                    currentPage = ((Number) currentPageObj).longValue();
                } else {
                    currentPage = Long.parseLong(currentPageObj.toString().trim());
                }
            }
            
            // 获取页面大小
            Object pageSizeObj = params.get("pageSize");
            if (pageSizeObj != null) {
                if (pageSizeObj instanceof Number) {
                    pageSize = ((Number) pageSizeObj).longValue();
                } else {
                    pageSize = Long.parseLong(pageSizeObj.toString().trim());
                }
            }
        } catch (NumberFormatException e) {
            throw new CustomizeException(ResultCodeEnum.ARGUMENT_VALID_ERROR.getCode(), 
                "分页参数格式错误，currentPage和pageSize必须为有效数字");
        }

        // 验证分页参数合理性
        if (currentPage < 1) {
            currentPage = 1L;
        }
        if (pageSize < 1) {
            pageSize = 10L;
        }
        if (pageSize > 100) {
            pageSize = 100L;
        }

        // 安全获取查询条件参数
        String categoryId = StringUtil.trim(params.get("categoryId"));
        String brandId = StringUtil.trim(params.get("brandId"));
        String keyword = StringUtil.trim(params.get("keyword"));
        String status = StringUtil.trim(params.get("publishStatus"));

        // 构建查询条件
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        
        // 添加分类ID条件
        if (StringUtil.isNotEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        // 添加品牌ID条件
        if (StringUtil.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        
        // 添加关键词搜索条件（支持ID精确匹配和名称模糊匹配）
        if (StringUtil.isNotEmpty(keyword)) {
            queryWrapper.and(w -> {
                // 尝试数字匹配ID，如果关键词是数字则精确匹配ID
                if (keyword.matches("\\d+")) {
                    w.eq("id", keyword).or().like("spu_name", keyword);
                } else {
                    // 非数字则只进行名称模糊匹配
                    w.like("spu_name", keyword);
                }
            });
        }
        
        // 添加发布状态条件
        if (StringUtil.isNotEmpty(status)) {
            try {
                int statusValue = Integer.parseInt(status);
                if (statusValue == 0 || statusValue == 1) {
                    queryWrapper.eq("publish_status", statusValue);
                }
            } catch (NumberFormatException e) {
                // 状态值格式错误时忽略该条件，不抛出异常
                // 这样可以保证查询的健壮性
            }
        }
        
        // 添加默认排序：按更新时间降序排列（新的在前）
        queryWrapper.orderByDesc("update_time");

        // 构建分页对象并执行查询
        Page<SpuInfo> page = new Page<>(currentPage, pageSize);
        Page<SpuInfo> spuInfoPage = this.page(page, queryWrapper);
        try {
            List<SpuInfo> records = spuInfoPage.getRecords();
            Page<SpuInfoVo> spuInfoVoPage = new Page<>();
            BeanUtils.copyProperties(spuInfoPage, spuInfoVoPage);
            
            // 使用Stream API优化数据转换
            List<SpuInfoVo> spuInfoVoList = records.stream().map(record -> {
                SpuInfoVo spuInfoVo = new SpuInfoVo();
                BeanUtils.copyProperties(record, spuInfoVo);
                
                // 安全地获取分类名称（包含异常处理）
                try {
                    String categoryName = myRedisCache.getCategoryNameById(record.getCategoryId());
                    spuInfoVo.setCategoryName(StringUtil.isNotEmpty(categoryName) ? categoryName : "未知分类");
                } catch (Exception e) {
                    spuInfoVo.setCategoryName("未知分类");
                    // 记录警告但不中断处理
                }
                
                // 安全地获取品牌名称（包含空值检查和异常处理）
                try {
                    var brand = myRedisCache.getBrandById(record.getBrandId());
                    String brandName = (brand != null && StringUtil.isNotEmpty(brand.getName())) ? brand.getName() : "未知品牌";
                    spuInfoVo.setBrandName(brandName);
                } catch (Exception e) {
                    spuInfoVo.setBrandName("未知品牌");
                    // 记录警告但不中断处理
                }
                
                return spuInfoVo;
            }).collect(Collectors.toList());
            
            spuInfoVoPage.setRecords(spuInfoVoList);
            return spuInfoVoPage;
        } catch (Exception e) {
            throw new CustomizeException(ResultCodeEnum.DATA_ERROR.getCode(), 
                "查询SPU信息时发生错误: " + e.getMessage());
        }
    }
}