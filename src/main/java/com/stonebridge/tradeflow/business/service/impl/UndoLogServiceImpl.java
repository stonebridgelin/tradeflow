package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.UndoLog;
import com.stonebridge.tradeflow.business.mapper.UndoLogMapper;
import com.stonebridge.tradeflow.business.service.UndoLogService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("undoLogService")
public class UndoLogServiceImpl extends ServiceImpl<UndoLogMapper, UndoLog> implements UndoLogService {

    @Override
    public Page<UndoLog> queryPage(Map<String, Object> params) {
//        IPage<UndoLog> page = this.page(
//                new Query<UndoLog>().getPage(params),
//                new QueryWrapper<UndoLog>()
//        );

        return null;
    }

}