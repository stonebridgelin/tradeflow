package com.stonebridge.tradeflow.business.entity.wms.vo;

import com.stonebridge.tradeflow.business.entity.wms.PurchaseDetail;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PurchaseDetailVo extends PurchaseDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String skuName;

    private String wareName;

    private String updateTimeStr;

    private String createTimeStr;

}
