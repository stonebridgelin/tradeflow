package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stonebridge.tradeflow.business.entity.wms.WareInfo;
import com.stonebridge.tradeflow.business.mapper.WareInfoMapper;
import com.stonebridge.tradeflow.business.service.WareInfoService;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoMapper, WareInfo> implements WareInfoService {

    @Override
    public List<WareInfo> querylist(String keyWord) {
        QueryWrapper<WareInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyWord)) {
            queryWrapper.eq("id", keyWord).or().like("name", keyWord).or().like("address", keyWord).or().like("areacode", keyWord);
        }
        return baseMapper.selectList(queryWrapper);
    }
}