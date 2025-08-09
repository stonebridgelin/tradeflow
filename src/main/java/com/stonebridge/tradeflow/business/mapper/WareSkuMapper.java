package com.stonebridge.tradeflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.business.entity.wms.WareSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
@Mapper
public interface WareSkuMapper extends BaseMapper<WareSku> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
