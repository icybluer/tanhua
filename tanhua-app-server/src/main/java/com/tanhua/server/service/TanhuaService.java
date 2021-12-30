package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.constant.CommonConstant;
import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.dto.QuestionDTO;
import com.tanhua.model.dto.RecommendUserDTO;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    public TodayBest todayBest() {
        //1. 获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2. 从mongo查询当前用户的推荐用户
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99D);
        }
        //3. 根据推荐的用户id，查询推荐用户的userInfo
        Long id = recommendUser.getUserId();
        UserInfo userInfo = userInfoApi.findById(id);
        //4. 封装为todayBest对象
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        //5. 返回
        return vo;
    }

    public PageResult recommendation(RecommendUserDTO dto) {
        //1. 获取当前用户的id
        Long userId = UserHolder.getUserId();

        //2. 从mongo查询当前用户的全部推荐用户
        List<RecommendUser> recommendUserList = recommendUserApi.queryRecommendUserList(userId);

        //3. 从mysql查询推荐用户的用户信息
        //3.1. 获取推荐用户id列表
        List<Long> ids = recommendUserList.stream()
                .map(RecommendUser::getUserId)
                .collect(Collectors.toList());
        //3.2. 查询条件封装
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        userInfo.setCity(dto.getCity());
        userInfo.setEducation(dto.getEducation());
        //3.3. 查询用户信息列表
        List<UserInfo> userInfoList = userInfoApi.findByIds(ids, userInfo);

        //4. 将匹配分值与用户信息封装到todayBest对象
        //4.1. 构建用户信息map，用户id作为键
        Map<Long, UserInfo> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
        //4.2. 封装todayBest对象列表
        List<TodayBest> list = recommendUserList.stream()
                // 从推荐用户列表中去掉不符合条件的推荐用户，即不存在与用户信息列表中的用户
                .filter(recommendUser -> userInfoMap.get(recommendUser.getUserId()) != null)
                // 调用TodayBest的init方法封装userInfo和recommendUser
                .map(recommendUser -> TodayBest.init(userInfoMap.get(recommendUser.getUserId()), recommendUser))
                // 根据todayBest的fateValue属性降序排序
                .sorted(Comparator.comparing(TodayBest::getFateValue).reversed())
                // 构造分页
                .skip((dto.getPage() - 1) * dto.getPagesize())
                .limit(dto.getPagesize())
                .collect(Collectors.toList());

        //5. 构造并返回PageResult对象
        Long count = (long) userInfoList.size();
        return new PageResult(dto.getPage(), dto.getPagesize(), count, list);
    }

    public TodayBest getPersonalInfo(Long id) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 获取推荐用户信息
        UserInfo userInfo = userInfoApi.findById(id);
        RecommendUser user = recommendUserApi.getRecommendUser(userId, id);
        // 封装vo对象并返回
        return TodayBest.init(userInfo, user);
    }

    @DubboReference
    private QuestionApi questionApi;

    public String getStrangerQuestion(Long userId) {
        Question q = questionApi.findByUserId(userId);
        return q == null ? CommonConstant.NO_QUESTION : q.getTxt();
    }

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    public void replyQuestion(QuestionDTO dto) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 获取当前用户的用户信息
        UserInfo userInfo = userInfoApi.findById(userId);
        // 组织环信消息
        Map map = new HashMap<>();
        map.put("userId", userId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + userId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", getStrangerQuestion(dto.getUserId()));
        map.put("reply", dto.getReply());
        String msg = JSON.toJSONString(map);
        // 发送环信信息
        Boolean result = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + dto.getUserId(), msg);
        if (!result) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
