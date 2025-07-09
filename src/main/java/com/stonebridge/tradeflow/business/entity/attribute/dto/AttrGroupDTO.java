package com.stonebridge.tradeflow.business.entity.attribute.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttrGroupDTO extends BasePageDTO {
    private String categoryId;
}
