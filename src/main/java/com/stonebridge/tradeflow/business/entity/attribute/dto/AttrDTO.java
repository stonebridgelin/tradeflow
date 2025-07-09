package com.stonebridge.tradeflow.business.entity.attribute.dto;

import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttrDTO extends BasePageDTO {
    private String categoryId;
    private String type;
}
