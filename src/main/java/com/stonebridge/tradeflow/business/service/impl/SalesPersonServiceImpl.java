package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.Salesperson;
import com.stonebridge.tradeflow.business.mapper.SalespersonMapper;
import com.stonebridge.tradeflow.business.service.SalespersonService;
import org.springframework.stereotype.Service;

@Service
public class SalesPersonServiceImpl extends ServiceImpl<SalespersonMapper, Salesperson> implements SalespersonService {
}
