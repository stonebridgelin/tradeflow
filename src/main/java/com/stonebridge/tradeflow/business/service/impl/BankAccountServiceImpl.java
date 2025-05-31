package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.BankAccount;
import com.stonebridge.tradeflow.business.mapper.BankAccountMapper;
import com.stonebridge.tradeflow.business.service.BankAccontService;
import org.springframework.stereotype.Service;

@Service
public class BankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccount> implements BankAccontService {
}
