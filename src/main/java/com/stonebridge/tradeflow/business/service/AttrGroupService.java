package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrGroupDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrGroupVO;

public interface AttrGroupService extends IService<AttrGroup> {
    Page<AttrGroupVO> queryPage(AttrGroupDTO attrGroupDTO);

    boolean save(AttrGroup attrGroup);

    AttrGroupVO selectAttrGroupById(String attrGroupId);
}
