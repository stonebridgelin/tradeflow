package com.stonebridge.tradeflow.business.entity.spu;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVo {
    private String spuName;
    private String spuDescription;
    private String categoryId;
    private String brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}