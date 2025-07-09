package com.stonebridge.tradeflow.business.entity.attribute.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttrDTO extends AttrGroupDTO{
    private String type;
}
