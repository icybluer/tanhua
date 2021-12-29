package com.itheima.test;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = AppServerApplication.class)
@RunWith(SpringRunner.class)
public class HuanXinTemplateTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Test
    public void testRegister() {
        huanXinTemplate.createUser("user01", "123456");
    }
}