package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.WareSku;
import com.stonebridge.tradeflow.business.mapper.WareSkuMapper;
import com.stonebridge.tradeflow.business.service.WareSkuService;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSku> implements WareSkuService {

    // 分页相关常量
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;
    private static final int MIN_PAGE = 1;
    private static final int MIN_LIMIT = 1;

    private final WareSkuMapper wareSkuMapper;

    @Autowired
    public WareSkuServiceImpl(WareSkuMapper wareSkuMapper) {
        this.wareSkuMapper = wareSkuMapper;
    }

    @Override
    public Page<WareSku> queryPage(Map<String, Object> params) {
        if (params == null) {
            params = Map.of();
        }

        // 构建查询条件
        QueryWrapper<WareSku> queryWrapper = new QueryWrapper<>();
        // SKU ID 查询条件
        String skuId = StringUtil.trim(params.get("skuId"));
        if (StringUtil.isNotEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        // 仓库 ID 查询条件
        String wareId = StringUtil.trim(params.get("wareId"));
        if (StringUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        // 解析分页参数
        int page = StringUtil.parseIntParameter(params, "page", DEFAULT_PAGE, MIN_PAGE, Integer.MAX_VALUE);
        int limit = StringUtil.parseIntParameter(params, "limit", DEFAULT_LIMIT, MIN_LIMIT, MAX_LIMIT);

        // 执行分页查询
        Page<WareSku> queryPage = new Page<>(page, limit);
        queryWrapper.orderByAsc("sku_id");

        log.debug("执行库存分页查询: page={}, limit={}, params={}", page, limit, params);
        return this.page(queryPage, queryWrapper);
    }






    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.如果还没有这个库存记录则新增
        QueryWrapper<WareSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId).eq("ware_id", wareId);
        List<WareSku> list = wareSkuMapper.selectList(queryWrapper);
        if (list.isEmpty()) {
            WareSku wareSkuEntity = new WareSku();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);

            wareSkuMapper.insert(wareSkuEntity);
        } else {
            wareSkuMapper.addStock(skuId, wareId, skuNum);
        }
    }
}