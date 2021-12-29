package com.itheima.test;

import com.github.dozermapper.core.Mapper;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.SettingsVO;
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
        SettingsVO vo = new SettingsVO();
        Settings settings = settingsApi.findByUserId(106L);
        if (settings != null) {
            vo = mapper.map(settings, SettingsVO.class);
        }
        System.out.println(vo);
    }

}
