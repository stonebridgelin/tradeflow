package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.PlatformInfo;
import com.stonebridge.tradeflow.business.mapper.PlatformInfoMapper;
import com.stonebridge.tradeflow.business.service.PlatformInfoService;
import org.springframework.stereotype.Service;

@Service
public class PlatformInfoServiceImpl extends ServiceImpl<PlatformInfoMapper, PlatformInfo> implements PlatformInfoService {
}
