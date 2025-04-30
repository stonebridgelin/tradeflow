package com.stonebridge.tradeflow.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    List<Long> findSysUserRoleByUserId(Long userId);

    void deleteAllRoleByUserId(Long userId);// 根据用户的id删除对应的所有roles数据


    void doAssign(@Param("userId") Long userId,
                                  @Param("roleId") Long roleId);        // 添加关联关系
}
