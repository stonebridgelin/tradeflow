package com.stonebridge.tradeflow.business.entity.wms.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
