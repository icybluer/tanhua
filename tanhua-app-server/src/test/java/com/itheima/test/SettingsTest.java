package com.itheima.test;

import com.github.dozermapper.core.Mapper;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.pojo.Settings;
import com.tanhua.model.domain.vo.SettingsVo;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = AppServerApplication.class)
@RunWith(SpringRunner.class)
public class SettingsTest {
    @DubboReference
    private SettingsApi settingsApi;

    @Autowired
    private Mapper mapper;

    @Test
    public void settingsTest(){
        SettingsVo vo = new SettingsVo();
        Settings settings = settingsApi.findByUserId(106L);
        if (settings != null) {
            vo = mapper.map(settings,SettingsVo.class);
        }
        System.out.println(vo);
    }

}
