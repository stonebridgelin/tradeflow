package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.entity.vo.SysRoleQueryVo;

public interface SysRoleService extends IService<SysRole> {

    JSONObject queryRolePage(Integer pageNum, Integer pageSize, SysRoleQueryVo roleQueryVo);

    void deleteById(Long roleId);
}
