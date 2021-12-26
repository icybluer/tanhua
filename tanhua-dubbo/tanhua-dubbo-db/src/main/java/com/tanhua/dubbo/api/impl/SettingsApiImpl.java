package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.mapper.SettingsMapper;
import com.tanhua.model.domain.pojo.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi {
    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long userId) {
        return settingsMapper.selectOne(Wrappers.<Settings>lambdaQuery().eq(Settings::getUserId,userId));
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }
}
