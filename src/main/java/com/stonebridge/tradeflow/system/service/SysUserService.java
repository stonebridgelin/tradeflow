package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.vo.SysUserQueryVo;

public interface SysUserService extends IService<SysUser> {

    JSONObject findByPage(Page<SysUser> page, SysUserQueryVo sysUserDto);
}
