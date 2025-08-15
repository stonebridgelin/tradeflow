package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.Purchase;
import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import com.stonebridge.tradeflow.business.entity.wms.vo.MergeVo;
import com.stonebridge.tradeflow.business.entity.wms.vo.PurchaseDoneVo;
import com.stonebridge.tradeflow.business.entity.wms.vo.PurchaseItemDoneVo;
import com.stonebridge.tradeflow.business.mapper.PurchaseMapper;
import com.stonebridge.tradeflow.business.service.PurchaseDetailService;
import com.stonebridge.tradeflow.business.service.PurchaseService;
import com.stonebridge.tradeflow.business.service.WareSkuService;
import com.stonebridge.tradeflow.common.constant.WareConstant;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {

    PurchaseDetailService purchaseDetailService;

    WareSkuService wareSkuService;

    @Autowired
    public PurchaseServiceImpl(PurchaseDetailService purchaseDetailService, WareSkuService wareSkuService) {
        this.purchaseDetailService = purchaseDetailService;
        this.wareSkuService = wareSkuService;
    }

    @Override
    public Page<Purchase> queryPage(Map<String, Object> params) {
        String status = StringUtil.parseStringParameter(params, "status", "");
        String keyWord = StringUtil.parseStringParameter(params, "key", "");
        int page = StringUtil.parseIntParameter(params, "page", 1,1,Integer.MAX_VALUE);
        int limit = StringUtil.parseIntParameter(params, "limit", 1,1,Integer.MAX_VALUE);

        QueryWrapper<Purchase> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyWord)) {
            queryWrapper.like("key", keyWord);
        }
        if (StringUtil.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        // 执行分页查询
        Page<Purchase> queryPage = new Page<>(page, limit);
        queryWrapper.orderByAsc("update_Time");

        return this.page(queryPage, queryWrapper);
    }

    @Override
    public Page<Purchase> queryPageUnreceive(Map<String, Object> params) {
        QueryWrapper<Purchase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0).or().eq("status", 1);
//        IPage<Purchase> page = this.page(new Query<Purchase>().getPage(params), queryWrapper);
        return null;
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        String purchaseId = String.valueOf(mergeVo.getPurchaseId());
        if (purchaseId == null) {
            //新建一个
            Purchase purchaseEntity = new Purchase();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = mergeVo.getItems();
        String finalPurchaseId = purchaseId;
        List<PurchaseDetail> list = items.stream().map(item -> {
            PurchaseDetail detailEntity = new PurchaseDetail();
            detailEntity.setId(item);
            detailEntity.setPurchaseId(Long.valueOf(finalPurchaseId));
//            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(list);
        Purchase purchaseEntity = new Purchase();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {
        //1.确认当前采购单是分配是新建或者已分配的状态
        List<Purchase> collect = ids.stream().map(id -> {
            Purchase purchase = this.getById(id);
            return purchase;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //2.改变采购单的状态
        this.updateBatchById(collect);
        //3.改变采购项的状态
        collect.forEach((item) -> {
            List<PurchaseDetail> list = purchaseDetailService.listDetailByPurchaseId(Long.valueOf(item.getId()));
            List<PurchaseDetail> purchaseDetailEntities = list.stream().map(entity -> {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setId(entity.getId());
//                purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });
    }


    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //1.改变采购单的状态
        Long id = purchaseDoneVo.getId();
        //2.改变采购项的状态
        boolean flag = true;
        List<PurchaseDetail> updates = new ArrayList<>();
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetail detailEntity = new PurchaseDetail();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
//                detailEntity.setStatus(item.getStatus());
            } else {
//                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //入库
                PurchaseDetail entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);
        //采购单
        Purchase purchase = new Purchase();
        purchase.setId(String.valueOf(id));
        purchase.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);
    }
}