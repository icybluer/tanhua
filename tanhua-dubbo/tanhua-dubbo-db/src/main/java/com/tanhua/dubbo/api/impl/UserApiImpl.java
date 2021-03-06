package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.mapper.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Administrator
 */
@DubboService
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getMobile, mobile));
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAll();
    }
}