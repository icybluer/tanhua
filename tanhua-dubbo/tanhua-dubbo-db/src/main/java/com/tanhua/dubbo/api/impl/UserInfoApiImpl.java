package com.tanhua.dubbo.api.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import com.tanhua.model.domain.dto.RecommendUserDto;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public List<UserInfo> findByIds(List<Long> ids, UserInfo userInfo) {
        LambdaQueryWrapper<UserInfo> wrapper = Wrappers.<UserInfo>lambdaQuery()
                .in(UserInfo::getId,ids)
                .eq(userInfo.getGender() != null, UserInfo::getGender, userInfo.getGender())
                .eq(userInfo.getEducation() != null, UserInfo::getEducation, userInfo.getEducation())
                .eq(userInfo.getCity() != null, UserInfo::getCity, userInfo.getCity())
                .lt(userInfo.getAge() != null, UserInfo::getAge, userInfo.getAge())
                .like(userInfo.getNickname() != null, UserInfo::getNickname, userInfo.getNickname());
        return userInfoMapper.selectList(wrapper);
    }

    @Override
    public List<UserInfo> findByIds(List<Long> ids) {
        return userInfoMapper.selectBatchIds(ids);
    }

    @Override
    public Page<UserInfo> findByIdsAndPage(List<Long> ids, UserInfo userInfo, Integer page, Integer pagesize) {
        //分页构造器
        Page<UserInfo> pageInfo = new Page<>(page, pagesize);
        //查询条件
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userInfo.getGender() != null, UserInfo::getGender, userInfo.getGender())
                .lt(userInfo.getAge() != null, UserInfo::getAge, userInfo.getGender())
                .like(userInfo.getNickname() != null, UserInfo::getNickname, userInfo.getNickname());
        //查询满足条件的数据，并分页
        userInfoMapper.selectPage(pageInfo, wrapper);
        //返回需要的数据
        return pageInfo;
    }

    @Override
    public Page<UserInfo> findByIdsAndPage(Set<Long> ids, UserInfo userInfo, Integer page, Integer pagesize) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId, ids)
                .eq(userInfo.getGender() != null, UserInfo::getGender, userInfo.getGender())
                .lt(userInfo.getAge() != null, UserInfo::getAge, userInfo.getAge())
                .like(userInfo.getNickname() != null, UserInfo::getNickname, userInfo.getNickname());
        Page<UserInfo> pageInfo = new Page<>(page, pagesize);
        userInfoMapper.selectPage(pageInfo, wrapper);
        return pageInfo;
    }
}
