package com.stonebridge.tradeflow.business.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BasePageDTO {
    private String page;
    private String limit;
    private String keyword;
}
