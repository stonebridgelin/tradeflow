package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import com.stonebridge.tradeflow.business.mapper.PurchaseDetailMapper;
import com.stonebridge.tradeflow.business.service.PurchaseDetailService;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements PurchaseDetailService {

    @Override
    public Page<PurchaseDetail> queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        String key = StringUtil.trim(params.get("key"));
        if (StringUtil.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = StringUtil.trim(params.get("status"));
        if (StringUtil.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        String wareId = StringUtil.trim(params.get("wareId"));
        if (StringUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
//        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), queryWrapper);
        return null;
    }

    @Override
    public List<PurchaseDetail> listDetailByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_id", id);
        List<PurchaseDetail> list = this.list(wrapper);
        return list;
    }
}