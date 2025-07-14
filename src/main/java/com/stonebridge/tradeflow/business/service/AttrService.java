package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrAttrgroupRelationDto;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;

import java.util.List;


public interface AttrService extends IService<Attr> {

    Page<AttrRespVo> queryPage(AttrDTO attrDTO);

    void saveAttr(AttrVo attr);

    AttrRespVo getAttrInfo(String attrId);

    void updateAttr(AttrVo attr);

    void deleteAttrById(String attrId);

    List<Attr> getAttrByAttrGoupId(String attrGroupId);

    void deleteRelation(List<AttrAttrgroupRelationDto> attrGroupRelationDtos);

    Page<Attr> getNoRelationAttr(String attrGroupId, BasePageDTO basePageDTO);
}

