package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;


public interface AttrService extends IService<Attr> {

    Page<AttrRespVo> queryPage(AttrDTO attrDTO);

    void saveAttr(AttrVo attr);

    AttrRespVo getAttrInfo(String attrId);

    void updateAttr(AttrVo attr);

    void deleteAttrById(String attrId);
}

