package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.mapper.BlackListMapper;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import com.tanhua.model.domain.pojo.BlackList;
import com.tanhua.model.domain.pojo.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {
    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public IPage<UserInfo> findByUserId(Long userId, Integer page, Integer size) {
        //1、构建分页参数对象Page
        Page pages = new Page(page, size);
        //2、调用方法分页（自定义编写 分页参数Page，sql条件参数）
        return userInfoMapper.findBlackList(pages, userId);
    }

    @Override
    public void remove(Long userId, Long uid) {
        blackListMapper.delete(Wrappers.<BlackList>lambdaQuery()
                .eq(BlackList::getUserId,userId)
                .eq(BlackList::getBlackUserId,uid)
        );
    }
}
