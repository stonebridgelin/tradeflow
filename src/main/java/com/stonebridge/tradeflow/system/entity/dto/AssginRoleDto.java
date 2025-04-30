package com.stonebridge.tradeflow.system.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssginRoleDto {

    private Long userId;				// 用户的id
    private List<Long> roleIds;		// 角色id

}
