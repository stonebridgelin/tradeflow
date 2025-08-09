package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.wms.Purchase;
import com.stonebridge.tradeflow.business.entity.wms.vo.MergeVo;
import com.stonebridge.tradeflow.business.entity.wms.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
public interface PurchaseService extends IService<Purchase> {

    Page<Purchase> queryPage(Map<String, Object> params);

    Page<Purchase> queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}

