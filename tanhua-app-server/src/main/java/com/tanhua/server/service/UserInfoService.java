package com.tanhua.server.service;

import com.github.dozermapper.core.Mapper;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.dto.UserInfoDto;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.ErrorResult;
import com.tanhua.model.domain.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {
    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    Mapper mapper;

    /**
     * 首次登录完善资料
     */
    public void save(UserInfoDto dto) {
        UserInfo userInfo = mapper.map(dto, UserInfo.class);
        userInfoApi.save(userInfo);
    }

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    /**
     * 上传保存用户头像
     */
    public void updateAvatar(MultipartFile pic, Long id) throws IOException {
        // 将头像上传至阿里云，得到文件存储路径
        String url = ossTemplate.upload(pic.getOriginalFilename(), pic.getInputStream());
        // 调用百度ai识别人脸
        boolean result = aipFaceTemplate.detect(url, "URL");
        // 人脸识别失败，抛出异常
        if (!result) {
            throw new BusinessException(ErrorResult.faceError());
        }
        // 识别成功，保存头像地址到数据库
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAvatar(url);
        userInfoApi.update(userInfo);
    }

    public UserInfoVo findById(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);
        return mapper.map(userInfo, UserInfoVo.class);
    }

    public void updateUserInfo(UserInfoDto dto) {
        UserInfo userInfo = mapper.map(dto, UserInfo.class);
        userInfoApi.update(userInfo);
    }
}
