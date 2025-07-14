package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;
import com.stonebridge.tradeflow.business.mapper.AttrAttrGroupRelationMapper;
import com.stonebridge.tradeflow.business.service.AttrAttrgroupRelationService;
import org.springframework.stereotype.Service;

@Service
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrGroupRelationMapper, AttrAttrgroupRelation> implements AttrAttrgroupRelationService {
    @Override
    public void deleteAttrGroupRelation(String attrGroupId) {
        QueryWrapper<AttrAttrgroupRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id", attrGroupId);
        this.remove(queryWrapper);
    }
}
