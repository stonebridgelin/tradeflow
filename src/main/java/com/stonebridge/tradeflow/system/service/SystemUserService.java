package com.stonebridge.tradeflow.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.entity.SystemUser;
import com.stonebridge.tradeflow.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SystemUserService extends ServiceImpl<UserMapper, SystemUser> {
    private final JdbcTemplate systemJdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public SystemUserService(@Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate,
                             RedisTemplate<String, Object> redisTemplate) {
        this.systemJdbcTemplate = systemJdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    public SystemUser getByIdWithCache(Long id) {
        String key = "system:user:" + id;
        SystemUser user = (SystemUser) redisTemplate.opsForValue().get(key);

        if (user == null) {
            user = baseMapper.selectById(id); // MyBatis-Plus 查询
            if (user != null) {
                redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS); // 缓存 1 小时
            }
        }
        return user;
    }

    public String getUsernameByIdJdbc(Long id) {
        return systemJdbcTemplate.queryForObject(
                "SELECT user_name FROM sys_user WHERE id = ?", String.class, id);
    }
}