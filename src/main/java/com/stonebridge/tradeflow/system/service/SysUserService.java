package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.entity.dto.AssginRoleDto;
import com.stonebridge.tradeflow.system.entity.vo.UserQueryVo;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    JSONObject findByPage(Page<SysUser> page, UserQueryVo userQueryVo);

    JSONObject getAllRoles(Long userId);

    void doAssign(AssginRoleDto assginRoleDto);

    Map<String, Object> getUserById(Long userId);

    ObjectNode getUserInfo(String userId);
}
