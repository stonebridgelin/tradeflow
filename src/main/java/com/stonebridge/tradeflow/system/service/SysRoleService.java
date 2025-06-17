package com.stonebridge.tradeflow.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.system.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    ObjectNode queryRolePage(Integer pageNum, Integer pageSize, String keyWord);

    void deleteById(Long roleId);

    List<String> getRoleCodesByUserId(Long userId);
}
