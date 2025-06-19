package com.stonebridge.tradeflow.business.entity.supplier.vo;

import com.stonebridge.tradeflow.business.entity.supplier.Supplier;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierVO extends Supplier {
    private List<String> categories;
}
