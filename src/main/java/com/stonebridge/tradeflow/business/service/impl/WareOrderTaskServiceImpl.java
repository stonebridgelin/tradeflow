package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.WareOrderTask;
import com.stonebridge.tradeflow.business.mapper.WareOrderTaskMapper;
import com.stonebridge.tradeflow.business.service.WareOrderTaskService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskMapper, WareOrderTask> implements WareOrderTaskService {

    @Override
    public Page<WareOrderTask> queryPage(Map<String, Object> params) {
//        IPage<WareOrderTaskEntity> page = this.page(
//                new Query<WareOrderTaskEntity>().getPage(params),
//                new QueryWrapper<WareOrderTaskEntity>()
//        );

        return null;
    }

}