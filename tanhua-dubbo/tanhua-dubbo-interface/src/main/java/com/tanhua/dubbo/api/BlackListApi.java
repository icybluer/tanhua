package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

public interface BlackListApi {
    IPage<UserInfo> findByUserId(Long userId, Integer page, Integer size);

    void remove(Long userId, Long uid);
}
