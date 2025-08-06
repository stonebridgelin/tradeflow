package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.product.ProductAttrValue;
import com.stonebridge.tradeflow.common.result.Result;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface ProductAttrValueService extends IService<ProductAttrValue> {

    Result<Object> queryPage(Map<String, Object> params);

    void saveProductAttr(List<ProductAttrValue> collect);

    void updateSpuAttr(String spuId, List<ProductAttrValue> entities);

    List<ProductAttrValue> baseAttrListForSpu(Long spuId);
}

