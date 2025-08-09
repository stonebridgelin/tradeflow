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

    @Autowired
    WareSkuMapper wareSkuDao;


    @Override
    public Page<WareSku> queryPage(Map<String, Object> params) {
        QueryWrapper<WareSku> queryWrapper = new QueryWrapper<>();
        String skuId = StringUtil.trim(params.get("skuId"));
        if (StringUtil.isNotEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = StringUtil.trim(params.get("wareId"));
        if (StringUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
//        IPage<WareSku> page = this.page(new Query<WareSku>().getPage(params), queryWrapper);
        return null;
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.如果还没有这个库存记录则新增
        QueryWrapper<WareSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId).eq("ware_id", wareId);
        List<WareSku> list = wareSkuDao.selectList(queryWrapper);
        if (list.isEmpty()) {
            WareSku wareSkuEntity = new WareSku();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);

            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }
}