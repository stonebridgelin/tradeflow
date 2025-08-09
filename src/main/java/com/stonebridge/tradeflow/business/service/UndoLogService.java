package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.wms.UndoLog;

import java.util.Map;

/**
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
public interface UndoLogService extends IService<UndoLog> {

    Page<UndoLog> queryPage(Map<String, Object> params);
}

