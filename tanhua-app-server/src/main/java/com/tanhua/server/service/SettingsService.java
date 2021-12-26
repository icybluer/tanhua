package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dozermapper.core.Mapper;
import com.tanhua.commons.constant.CommonConstant;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.dto.QuestionDto;
import com.tanhua.model.domain.dto.SettingsDto;
import com.tanhua.model.domain.pojo.Question;
import com.tanhua.model.domain.pojo.Settings;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.domain.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    private Mapper mapper;

    /**
     * 用户通用设置-读取
     */
    public SettingsVo settings() {
        SettingsVo vo = new SettingsVo();
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        vo.setId(userId);
        //获取当前用户手机号
        String phone = UserHolder.getMobile();
        vo.setPhone(phone);
        //根据id查询通用设置
        Settings settings = settingsApi.findByUserId(userId);
        if (settings != null) {
            vo.setLikeNotification(settings.getLikeNotification());
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
        }
        //根据id查询陌生人问题
        Question question = questionApi.findByUserId(userId);
        String strangerQuestion = question != null ? question.getTxt() : CommonConstant.NO_QUESTION;
        vo.setStrangerQuestion(strangerQuestion);

        return vo;
    }

    /**
     * 设置陌生人问题 - 保存
     */
    public void setQuestion(QuestionDto dto) {
        //1. 获取当前用户的id，
        Long userId = UserHolder.getUserId();
        String content = dto.getContent();
        //2. 保存到数据库
        //2.1. 根据用户id查询数据库是否已有数据
        Question question = questionApi.findByUserId(userId);

        if (question != null) {
            //2.2. 有则修改原有数据
            question.setTxt(content);
            questionApi.update(question);
        } else {
            //2.3. 没有则添加
            question = Question.builder()
                    .userId(userId)
                    .txt(content)
                    .build();
            questionApi.save(question);
        }
    }

    /**
     * 通知设置 - 保存
     */
    public void setNotifications(SettingsDto dto) {
        //1. 获取当前的用户id
        Long userId = UserHolder.getUserId();
        //2. 将数据保存至数据库
        //2.1. 根据用户id查询数据库是否已有数据
        Settings settings = settingsApi.findByUserId(userId);
        if (settings != null) {
            //2.2. 有则修改原有数据
            settings.setLikeNotification(dto.getLikeNotification());
            settings.setPinglunNotification(dto.getPinglunNotification());
            settings.setGonggaoNotification(dto.getGonggaoNotification());
            settingsApi.update(settings);
        } else {
            //2.3. 没有则添加
            settings = Settings.builder()
                    .userId(userId)
                    .likeNotification(dto.getLikeNotification())
                    .pinglunNotification(dto.getPinglunNotification())
                    .gonggaoNotification(dto.getGonggaoNotification())
                    .build();
            settingsApi.save(settings);
        }
    }

    @DubboReference
    private BlackListApi blackListApi;

    /**
     * 黑名单 - 翻页列表
     */
    public PageResult blacklist(Integer page, Integer size) {
        //1、获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2、调用API查询用户的黑名单分页列表  Ipage对象
        IPage<UserInfo> iPage = blackListApi.findByUserId(userId, page, size);
        //3、对象转化，将查询的Ipage对象的内容封装到PageResult中
        PageResult pr = new PageResult(page, size, iPage.getTotal(), iPage.getRecords());
        //4、返回
        return pr;
    }

    public void removeBlackList(Long uid) {
        //1. 获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2. 根据id和uid删除对应的黑名单数据
        blackListApi.remove(userId, uid);
    }
}
