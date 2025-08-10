package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.wms.WareInfo;

import java.util.List;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
public interface WareInfoService extends IService<WareInfo> {

    List<WareInfo> querylist(String keyWord);
}

