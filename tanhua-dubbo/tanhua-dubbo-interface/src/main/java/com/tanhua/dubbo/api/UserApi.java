package com.tanhua.dubbo.api;

import com.tanhua.model.domain.pojo.User;

public interface UserApi {
    /**
     * 根据手机号查询用户信息
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 保存用户信息
     *
     * @param user
     * @return 新用户ID
     */
    Long save(User user);
}