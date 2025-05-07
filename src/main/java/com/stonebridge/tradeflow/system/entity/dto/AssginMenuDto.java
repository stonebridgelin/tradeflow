package com.stonebridge.tradeflow.system.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "角色菜单权限分配", description = "用于为角色分配菜单权限时接收对应关系")
@Data
public class AssginMenuDto {
    @Schema(description = "角色的id")
    private String roleId;

    @Schema(description = "菜单id列表")
    private List<String> menuIds;
}
