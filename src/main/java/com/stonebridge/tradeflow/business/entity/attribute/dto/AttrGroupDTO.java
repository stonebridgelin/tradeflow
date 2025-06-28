package com.stonebridge.tradeflow.business.entity.attribute.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttrGroupDTO {
    private String page;
    private String limit;
    private String categoryId;
    private String keyword;
}
