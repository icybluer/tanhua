package com.itheima.test;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = AppServerApplication.class)
@RunWith(SpringRunner.class)
public class HuanXinTemplateTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Test
    public void testRegister() {
        huanXinTemplate.createUser("user01", "123456");
    }

    @DubboReference
    private UserApi userApi;

    //批量注册
    @Test
    public void register() {
        List<User> users = userApi.findAll();
        for (User user : users) {
            Boolean create = huanXinTemplate.createUser("hx" + user.getId(), "123456");
            if (create){
                user.setHxUser("hx" + user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }
    }
}