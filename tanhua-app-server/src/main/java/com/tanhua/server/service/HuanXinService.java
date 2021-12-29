package com.tanhua.server.service;

import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinService {
    @DubboReference
    private UserApi userApi;

    public HuanXinUserVo getHuanXinUser() {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 获取环信用户名
        return new HuanXinUserVo("hx" + userId, Constants.INIT_PASSWORD);
    }
}
