package com.stonebridge.tradeflow.system.service.impl;

import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.utils.PasswordUtils;
import com.stonebridge.tradeflow.system.entity.User;
import com.stonebridge.tradeflow.system.entity.dto.LoginRequest;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.ACCOUNT_ERROR;
import static com.stonebridge.tradeflow.common.result.ResultCodeEnum.LOGIN_MOBLE_ERROR;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    private SysRoleMapper sysRoleMapper;

    private JdbcTemplate systemJdbcTemplate;

    @Autowired
    public AuthorizeServiceImpl(SysRoleMapper sysRoleMapper, @Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate) {
        this.sysRoleMapper = sysRoleMapper;
        this.systemJdbcTemplate = systemJdbcTemplate;
    }


    @Override
    public String loginCheck(LoginRequest request) {
        String sql = "select * from user where username = ?";
        User user = systemJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), request.getUsername().trim());
        if (user == null) {
            throw new CustomizeException(LOGIN_MOBLE_ERROR.getCode(), LOGIN_MOBLE_ERROR.getMessage());
        } else if (PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            return "x111x111";
        } else {
            throw new CustomizeException(ACCOUNT_ERROR.getCode(), ACCOUNT_ERROR.getMessage());
        }
    }
}
