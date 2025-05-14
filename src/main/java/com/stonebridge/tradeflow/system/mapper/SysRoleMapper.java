package com.stonebridge.tradeflow.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.entity.vo.SysRoleQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolePage(@Param("vo") SysRoleQueryVo vo);

    List<SysRole> findAllRoles();
}
