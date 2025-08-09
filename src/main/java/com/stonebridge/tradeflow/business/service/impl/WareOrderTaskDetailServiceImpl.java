package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.wms.WareOrderTaskDetail;
import com.stonebridge.tradeflow.business.mapper.WareOrderTaskDetailMapper;
import com.stonebridge.tradeflow.business.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailMapper, WareOrderTaskDetail> implements WareOrderTaskDetailService {

    @Override
    public Page<WareOrderTaskDetail> queryPage(Map<String, Object> params) {
//        IPage<WareOrderTaskDetail> page = this.page(
//                new Query<WareOrderTaskDetail>().getPage(params),
//                new QueryWrapper<WareOrderTaskDetail>()
//        );

        return null;
    }

}