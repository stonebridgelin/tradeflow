package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SkuInfo;
import com.stonebridge.tradeflow.business.mapper.SkuInfoMapper;
import com.stonebridge.tradeflow.business.service.SkuInfoService;
import com.stonebridge.tradeflow.common.utils.StringUtil;
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
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * 根据条件分页查询SKU信息
     * 
     * @param params 查询参数，包含以下字段：
     *               - key: 关键词（可选，模糊匹配SKU名称或精确匹配SKU ID）
     *               - catelogId: 分类ID（可选，值为"0"时忽略）
     *               - brandId: 品牌ID（可选，值为"0"时忽略）
     *               - min: 最低价格（可选）
     *               - max: 最高价格（可选，值为"0"时忽略）
     *               - currentPage: 当前页码（必需，默认为1）
     *               - pageSize: 每页大小（必需，默认为10，最大为100）
     * @return SKU分页结果
     */
    @Override
    public Page<SkuInfo> queryPageByCondition(Map<String, Object> params) {
        // 参数空值检查
        if (params == null) {
            params = new java.util.HashMap<>();
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
            // 分页参数格式错误时使用默认值
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

        // 构建查询条件
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        
        // 添加关键词搜索条件（支持SKU ID精确匹配和SKU名称模糊匹配）
        String key = StringUtil.trim(params.get("key"));
        if (StringUtil.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                // 尝试数字匹配SKU ID，如果关键词是数字则精确匹配ID
                if (key.matches("\\d+")) {
                    w.eq("sku_id", key).or().like("sku_name", key);
                } else {
                    // 非数字则只进行名称模糊匹配
                    w.like("sku_name", key);
                }
            });
        }
        
        // 添加分类ID条件
        String catelogId = StringUtil.trim(params.get("catelogId"));
        if (StringUtil.isNotEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("category_id", catelogId);
        }
        
        // 添加品牌ID条件
        String brandId = StringUtil.trim(params.get("brandId"));
        if (StringUtil.isNotEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        
        // 添加价格范围条件
        String min = StringUtil.trim(params.get("min"));
        if (StringUtil.isNotEmpty(min)) {
            try {
                queryWrapper.ge("price", min);
            } catch (NumberFormatException e) {
                // 最小价格格式错误时忽略该条件
            }
        }
        
        String max = StringUtil.trim(params.get("max"));
        if (StringUtil.isNotEmpty(max) && !"0".equalsIgnoreCase(max)) {
            try {
                queryWrapper.le("price", max);
            } catch (NumberFormatException e) {
                // 最大价格格式错误时忽略该条件
            }
        }
        
        // 添加默认排序：按SKU ID降序排列（新的在前）
        queryWrapper.orderByDesc("sku_id");

        // 构建分页对象并执行查询
        Page<SkuInfo> page = new Page<>(currentPage, pageSize);
        return this.page(page, queryWrapper);
    }
}