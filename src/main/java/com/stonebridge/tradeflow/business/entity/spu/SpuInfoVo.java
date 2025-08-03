package com.stonebridge.tradeflow.business.entity.spu;

import com.stonebridge.tradeflow.business.entity.product.SpuInfo;
import lombok.Data;

@Data
public class SpuInfoVo extends SpuInfo {
    private String brandName;
    private String categoryName;
}
