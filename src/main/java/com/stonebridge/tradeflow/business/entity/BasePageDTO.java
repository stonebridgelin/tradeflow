package com.stonebridge.tradeflow.business.entity;

import lombok.Data;

@Data
public class BasePageDTO {
    private String page;
    private String limit;
    private String keyword;
}
