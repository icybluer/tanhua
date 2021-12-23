package com.tanhua.dubbo.api;

import com.tanhua.model.domain.pojo.UserInfo;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);
}
