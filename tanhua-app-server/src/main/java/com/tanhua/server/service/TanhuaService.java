package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.dto.RecommendUserDto;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.domain.vo.TodayBest;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
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

    public PageResult recommendation(RecommendUserDto dto) {
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

}
