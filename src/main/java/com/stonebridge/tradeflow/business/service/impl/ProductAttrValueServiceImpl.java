package com.stonebridge.tradeflow.business.service.impl;

import com.stonebridge.tradeflow.business.entity.product.ProductAttrValue;
import com.stonebridge.tradeflow.business.mapper.ProductAttrValueMapper;
import com.stonebridge.tradeflow.business.service.ProductAttrValueService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueMapper, ProductAttrValue> implements ProductAttrValueService {

    @Override
    public Result<Object> queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public void saveProductAttr(List<ProductAttrValue> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValue> baseAttrlistforspu(Long spuId) {
        List<ProductAttrValue> entities = this.baseMapper.selectList(new QueryWrapper<ProductAttrValue>().eq("spu_id", spuId));
        return entities;
    }

    @Transactional
    @Override
    public void updateSpuAttr(String spuId, List<ProductAttrValue> entities) {
        //1、删除这个spuId之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValue>().eq("spu_id",spuId));


        List<ProductAttrValue> collect = entities.stream().map(item -> {item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}