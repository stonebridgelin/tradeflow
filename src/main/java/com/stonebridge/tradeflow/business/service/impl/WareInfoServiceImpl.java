package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stonebridge.tradeflow.business.entity.wms.WareInfo;
import com.stonebridge.tradeflow.business.mapper.WareInfoMapper;
import com.stonebridge.tradeflow.business.service.WareInfoService;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoMapper, WareInfo> implements WareInfoService {

    @Override
    public Page<WareInfo> queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfo> queryWrapper = new QueryWrapper<>();
        String key = StringUtil.trim(params.get("key"));
        if (StringUtil.isNotEmpty(key)) {
            queryWrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }
//        IPage<WareInfo> page = this.page(new Query<WareInfo>().getPage(params), queryWrapper);
        return null;
    }
}