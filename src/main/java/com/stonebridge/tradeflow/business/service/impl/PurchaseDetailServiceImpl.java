package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import com.stonebridge.tradeflow.business.entity.wms.vo.PurchaseDetailVo;
import com.stonebridge.tradeflow.business.mapper.PurchaseDetailMapper;
import com.stonebridge.tradeflow.business.mapper.SkuInfoMapper;
import com.stonebridge.tradeflow.business.mapper.WareInfoMapper;
import com.stonebridge.tradeflow.business.service.PurchaseDetailService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.utils.DateUtil;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import com.stonebridge.tradeflow.system.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements PurchaseDetailService {

    private final SkuInfoMapper skuInfoMapper;

    private final WareInfoMapper wareInfoMapper;

    private final MyRedisCache myRedisCache;

    @Autowired
    public PurchaseDetailServiceImpl(SkuInfoMapper skuInfoMapper, WareInfoMapper wareInfoMapper, MyRedisCache myRedisCache) {
        this.skuInfoMapper = skuInfoMapper;
        this.wareInfoMapper = wareInfoMapper;
        this.myRedisCache = myRedisCache;
    }

    @Override
    public Page<PurchaseDetailVo> queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetail> queryWrapper = new QueryWrapper<>();
        String key = StringUtil.parseStringParameter(params, "key", "");
        if (StringUtil.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = StringUtil.parseStringParameter(params, "status", "");
        if (StringUtil.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        String wareId = StringUtil.parseStringParameter(params, "wareId", "");
        if (StringUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        int page = StringUtil.parseIntParameter(params, "page", 1, 1, Integer.MAX_VALUE);
        int limit = StringUtil.parseIntParameter(params, "limit", 1, 1, Integer.MAX_VALUE);
        // 执行分页查询
        Page<PurchaseDetail> queryPage = new Page<>(page, limit);
        queryWrapper.orderByDesc("id");
        Page<PurchaseDetail> purchaseDetailPage = this.page(queryPage, queryWrapper);
        List<PurchaseDetail> purchaseDetailList = purchaseDetailPage.getRecords();
        Set<Long> wareIdSet = new HashSet<>();
        Set<Long> skuIdSet = new HashSet<>();
        purchaseDetailList.forEach(purchaseDetail -> {
            wareIdSet.add(purchaseDetail.getWareId());
            skuIdSet.add(purchaseDetail.getSkuId());
        });
        Map<Long, String> skuNameMap = new HashMap<>();
        skuIdSet.forEach(skuId -> {
            skuNameMap.put(skuId, skuInfoMapper.selectById(skuId).getSkuName());
        });
        Map<Long, String> wareNameMap = new HashMap<>();
        wareIdSet.forEach(id -> {
            wareNameMap.put(id, wareInfoMapper.selectById(id).getName());
        });

        List<PurchaseDetailVo> purchaseDetailVoList = new ArrayList<>();
        for (PurchaseDetail purchaseDetail : purchaseDetailList) {
            Long skuId = purchaseDetail.getSkuId();
            Long wareInfoId = purchaseDetail.getWareId();
            PurchaseDetailVo purchaseDetailVo = new PurchaseDetailVo();
            BeanUtils.copyProperties(purchaseDetail, purchaseDetailVo);
            if (skuId != null) {
                purchaseDetailVo.setSkuName(skuNameMap.get(skuId));
            }
            if (wareInfoId != null) {
                purchaseDetailVo.setWareName(wareNameMap.get(wareInfoId));
            }
            if (purchaseDetail.getCreateTime() != null) {
                purchaseDetailVo.setCreateTimeStr(DateUtil.format(purchaseDetail.getCreateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
            }
            if (purchaseDetail.getUpdateTime() != null) {
                purchaseDetailVo.setUpdateTimeStr(DateUtil.format(purchaseDetail.getUpdateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
            }
            if (purchaseDetail.getCreateBy() != null) {
                SysUser createUser = myRedisCache.getSysUserById(purchaseDetail.getCreateBy());
                purchaseDetailVo.setCreateBy(createUser != null ? createUser.getUsername() : "");
            }
            if (purchaseDetail.getUpdateBy() != null) {
                SysUser updateUser = myRedisCache.getSysUserById(purchaseDetail.getUpdateBy());
                purchaseDetailVo.setUpdateBy(updateUser != null ? updateUser.getUsername() : "");
            }
            purchaseDetailVoList.add(purchaseDetailVo);
        }
        Page<PurchaseDetailVo> purchaseDetailVoPage = new Page<>(page, limit);
        BeanUtils.copyProperties(purchaseDetailPage, purchaseDetailVoPage);
        purchaseDetailVoPage.setRecords(purchaseDetailVoList);
        return purchaseDetailVoPage;
    }

    @Override
    public List<PurchaseDetail> listDetailByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_id", id);
        return this.list(wrapper);
    }
}