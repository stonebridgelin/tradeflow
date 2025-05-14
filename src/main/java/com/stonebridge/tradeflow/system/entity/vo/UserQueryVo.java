package com.stonebridge.tradeflow.system.entity.vo;

import lombok.Data;

@Data
public class UserQueryVo {

    private String keyword;
    private String createTimeBegin;
    private String createTimeEnd;
}
