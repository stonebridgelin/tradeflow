package com.stonebridge.tradeflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.business.entity.wms.Purchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@Mapper
public interface PurchaseMapper extends BaseMapper<Purchase> {

}
