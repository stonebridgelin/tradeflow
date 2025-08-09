package com.stonebridge.tradeflow.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stonebridge.tradeflow.business.entity.wms.WareInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@Mapper
public interface WareInfoMapper extends BaseMapper<WareInfo> {

}
