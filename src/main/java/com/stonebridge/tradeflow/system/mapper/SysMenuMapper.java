package com.stonebridge.tradeflow.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectAll();
}
