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

    /**
     * 保存SPU完整信息（包括基本信息、描述、图片、规格参数和SKU信息）
     * 
     * 保存顺序：主表数据 -> 子表数据 -> SKU相关数据
     * 涉及表：
     * 1. pms_spu_info (SPU基本信息)
     * 2. pms_spu_info_desc (SPU描述信息)
     * 3. pms_spu_images (SPU图片集)
     * 4. pms_product_attr_value (SPU规格参数)
     * 5. pms_sku_info (SKU基本信息)
     * 6. pms_sku_images (SKU图片)
     * 7. pms_sku_sale_attr_value (SKU销售属性值)
     * 
     * @param spuSaveVo SPU保存数据传输对象，包含所有需要保存的SPU相关信息
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        Date now = new Date();
        
        // ==================== 第一步：保存SPU基本信息 ====================
        // 保存SPU基本信息到 pms_spu_info 表
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        spuInfo.setCreateTime(now);
        spuInfo.setUpdateTime(now);
        this.saveBaseSpuInfo(spuInfo);
        String spuId = spuInfo.getId(); // 获取生成的SPU主键ID，用于关联子表

        // ==================== 第二步：保存SPU描述信息 ====================
        // 保存SPU的描述图片信息到 pms_spu_info_desc 表
        List<String> decripts = spuSaveVo.getDecript();
        SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
        spuInfoDesc.setSpuId(spuId);
        spuInfoDesc.setDescription(String.join(",", decripts)); // 将描述图片URL数组转为逗号分隔字符串
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        // ==================== 第三步：保存SPU图片集 ====================
        // 保存SPU的商品图片集合到 pms_spu_images 表
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuId, images);

        // ==================== 第四步：保存SPU规格参数 ====================
        // 保存SPU的规格参数（基本属性）到 pms_product_attr_value 表
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValue> productAttrValues = baseAttrs.stream().map(item -> {
            ProductAttrValue productAttrValue = new ProductAttrValue();
            productAttrValue.setAttrId(String.valueOf(item.getAttrId()));
            productAttrValue.setAttrName(attrService.getById(item.getAttrId()).getAttrName()); // 通过属性ID查询属性名称
            productAttrValue.setAttrValue(item.getAttrValues());
            productAttrValue.setQuickShow(String.valueOf(item.getShowDesc())); // 是否在详情页快速展示
            productAttrValue.setSpuId(spuId);
            return productAttrValue;
        }).collect(Collectors.toList());
        valueService.saveProductAttr(productAttrValues);

        // ==================== 第五步：保存SKU相关信息 ====================
        // 保存当前SPU对应的所有SKU信息及其子表数据
        List<Skus> skusList = spuSaveVo.getSkus();
        if (!skusList.isEmpty()) {
            for (Skus sku : skusList) {
                // 查找SKU的默认图片URL
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                        break; // 找到默认图片后跳出循环
                    }
                }
                
                // 5.1 保存SKU基本信息到 pms_sku_info 表
                // 数据映射：从 Skus VO 拷贝到 SkuInfo 实体
                // 包括：skuName、price、skuTitle、skuSubtitle 等基本信息
                // 补充：brandId、categoryId（来自SPU）、spuId、defaultImg、saleCount
                SkuInfo skuInfo = new SkuInfo();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setBrandId(spuInfo.getBrandId());     // 继承SPU的品牌ID
                skuInfo.setCategoryId(spuInfo.getCategoryId()); // 继承SPU的分类ID
                skuInfo.setSaleCount(0L);                     // 新商品销量初始化为0
                skuInfo.setSpuId(spuId);                      // 关联SPU主键
                skuInfo.setSkuDefaultImg(defaultImg);         // 设置默认显示图片
                skuInfoService.saveSkuInfo(skuInfo);
                String skuId = skuInfo.getSkuId(); // 获取生成的SKU主键ID

                // 5.2 保存SKU图片信息到 pms_sku_images 表
                // 将SKU的图片集合转换为数据库实体并过滤掉空图片URL
                List<SkuImages> imagesEntities = sku.getImages().stream().map(img -> {
                    SkuImages skuImage = new SkuImages();
                    skuImage.setSkuId(skuId);
                    skuImage.setImgUrl(img.getImgUrl());
                    skuImage.setDefaultImg(img.getDefaultImg());
                    return skuImage;
                }).filter(entity -> {
                    // 过滤条件：只保留有效的图片URL（非空）
                    return !StringUtil.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                // 5.3 保存SKU销售属性值到 pms_sku_sale_attr_value 表
                // 销售属性：如颜色、尺寸等影响价格和库存的属性
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValue> skuSaleAttrValues = attrs.stream().map(attr -> {
                    SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                    BeanUtils.copyProperties(attr, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuId); // 关联SKU主键
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
                queryWrapper.eq("publish_status", statusValue);
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

    /**
     * 根据spu的主键id删除，spu的信息以及涉及到的子表上的数据
     * 删除顺序：先删除子表数据，最后删除主表数据
     * 涉及表：
     * 1. pms_sku_sale_attr_value (SKU销售属性值)
     * 2. pms_sku_images (SKU图片)
     * 3. pms_sku_info (SKU信息)
     * 4. pms_product_attr_value (SPU规格参数)
     * 5. pms_spu_images (SPU图片)
     * 6. pms_spu_info_desc (SPU描述)
     * 7. pms_spu_info (SPU基本信息)
     * @param spuId SPU主键ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSpuById(String spuId) {
        if (StringUtil.isEmpty(spuId)) {
            throw new CustomizeException(ResultCodeEnum.ARGUMENT_VALID_ERROR.getCode(), "SPU ID不能为空");
        }

        try {
            // 1. 首先查询该SPU下的所有SKU ID
            QueryWrapper<SkuInfo> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.eq("spu_id", spuId);
            List<SkuInfo> skuInfos = skuInfoService.list(skuQueryWrapper);
            
            if (!skuInfos.isEmpty()) {
                // 获取所有SKU的ID集合
                List<String> skuIds = skuInfos.stream()
                    .map(SkuInfo::getSkuId)
                    .collect(Collectors.toList());

                // 2. 删除SKU销售属性值数据 (pms_sku_sale_attr_value)
                if (!skuIds.isEmpty()) {
                    QueryWrapper<SkuSaleAttrValue> skuSaleAttrWrapper = new QueryWrapper<>();
                    skuSaleAttrWrapper.in("sku_id", skuIds);
                    skuSaleAttrValueService.remove(skuSaleAttrWrapper);
                }

                // 3. 删除SKU图片数据 (pms_sku_images)
                if (!skuIds.isEmpty()) {
                    QueryWrapper<SkuImages> skuImagesWrapper = new QueryWrapper<>();
                    skuImagesWrapper.in("sku_id", skuIds);
                    skuImagesService.remove(skuImagesWrapper);
                }

                // 4. 删除SKU基本信息 (pms_sku_info)
                skuInfoService.remove(skuQueryWrapper);
            }

            // 5. 删除SPU规格参数 (pms_product_attr_value)
            QueryWrapper<ProductAttrValue> attrValueWrapper = new QueryWrapper<>();
            attrValueWrapper.eq("spu_id", spuId);
            valueService.remove(attrValueWrapper);

            // 6. 删除SPU图片 (pms_spu_images)
            QueryWrapper<SpuImages> spuImagesWrapper = new QueryWrapper<>();
            spuImagesWrapper.eq("spu_id", spuId);
            spuImagesService.remove(spuImagesWrapper);

            // 7. 删除SPU描述信息 (pms_spu_info_desc)
            QueryWrapper<SpuInfoDesc> spuDescWrapper = new QueryWrapper<>();
            spuDescWrapper.eq("spu_id", spuId);
            spuInfoDescService.remove(spuDescWrapper);

            // 8. 最后删除SPU基本信息 (pms_spu_info)
            this.removeById(spuId);

        } catch (Exception e) {
            throw new CustomizeException(ResultCodeEnum.DATA_ERROR.getCode(), 
                "删除SPU数据时发生错误: " + e.getMessage());
        }
    }
}