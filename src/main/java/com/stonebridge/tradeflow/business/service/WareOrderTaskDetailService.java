package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.wms.WareOrderTaskDetail;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetail> {

    Page<WareOrderTaskDetail> queryPage(Map<String, Object> params);
}

