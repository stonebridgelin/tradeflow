package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.product.SpuInfoDesc;
import com.stonebridge.tradeflow.business.mapper.SpuInfoDescMapper;
import com.stonebridge.tradeflow.business.service.SpuInfoDescService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescMapper, SpuInfoDesc> implements SpuInfoDescService {

    @Override
    public Page<SpuInfoDesc> queryPage(Map<String, Object> params) {
//        IPage<SpuInfoDesc> page = this.page(
//                new Query<SpuInfoDesc>().getPage(params),
//                new QueryWrapper<SpuInfoDesc>()
//        );
//
//        return new PageUtils(page);
        return null;
    }

    /**
     * 保存spu的描述图片 pms_spu_info_desc
     *
     * @param descEntity SpuInfoDescEntity对象
     */
    @Override
    public void saveSpuInfoDesc(SpuInfoDesc descEntity) {
        this.baseMapper.insert(descEntity);
    }
}