package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.mapper.SysUserMapper;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

}
