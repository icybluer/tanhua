package com.itheima.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class BlackListTest {
    @DubboReference
    private BlackListApi blackListApi;

    @Test
    public void blackList() {
        IPage<UserInfo> page = blackListApi.findByUserId(106L, 3, 2);
        System.out.println(JSON.toJSONString(page, true));
    }

    @Test
    public void removeBlackList() {
        blackListApi.remove(110L, 2L);
    }
}
