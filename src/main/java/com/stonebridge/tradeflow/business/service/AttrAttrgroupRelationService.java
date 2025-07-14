package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;

public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelation> {
    void deleteAttrGroupRelation(String attrGroupId);
}
