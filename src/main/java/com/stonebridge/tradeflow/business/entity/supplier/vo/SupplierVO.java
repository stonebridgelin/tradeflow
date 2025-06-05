package com.stonebridge.tradeflow.business.entity.supplier.vo;

import com.stonebridge.tradeflow.business.entity.supplier.Supplier;
import lombok.Data;

import java.util.List;

@Data
public class SupplierVO extends Supplier {
    private List<String> categories;
}
