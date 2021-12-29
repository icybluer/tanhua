package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Set;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);

    List<UserInfo> findByIds(List<Long> ids, UserInfo userInfo);

    List<UserInfo> findByIds(List<Long> ids);

    Page<UserInfo> findByIdsAndPage(List<Long> ids, UserInfo userInfo, Integer page, Integer pagesize);

    Page<UserInfo> findByIdsAndPage(Set<Long> ids, UserInfo userInfo, Integer page, Integer pagesize);
}
