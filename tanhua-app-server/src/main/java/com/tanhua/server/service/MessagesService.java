package com.tanhua.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dozermapper.core.Mapper;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.constant.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.ContactVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVO;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessagesService {
    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private Mapper mapper;

    public UserInfoVO getUserInfoByHuanXinId(String huanxinId) {
        // 通过用户环信id得到用户id
        String userId = huanxinId.toLowerCase().replaceAll(Constants.HX_USER_PREFIX, "");
        // 查询用户详情
        UserInfo userInfo = userInfoApi.findById(Long.valueOf(userId));
        //// 封装VO对象
        //UserInfoVO vo = new UserInfoVO();
        //BeanUtils.copyProperties(userInfo, vo);
        //// 返回结果
        //return vo;
        return mapper.map(userInfo, UserInfoVO.class);
    }

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private FriendApi friendApi;

    public void contacts(Long friendId) {
        // 获取当前用户的id
        Long userId = UserHolder.getUserId();
        // 添加好友关系到环信
        Boolean result = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + userId,
                Constants.HX_USER_PREFIX + friendId);
        if (!result) {
            throw new BusinessException(ErrorResult.error());
        }
        // 添加好友关系到mongodb
        friendApi.save(userId, friendId);
    }

    public PageResult getFriends(Integer page, Integer pagesize, String keyword) {
        // 获取当前用户的id
        Long userId = UserHolder.getUserId();
        // 根据用户id查询当前用户的好友列表
        List<Friend> friendList = friendApi.getFriends(userId);
        // 从好友列表中获得好友id列表
        List<Long> friendIds = friendList.stream().map(Friend::getFriendId).collect(Collectors.toList());
        // 根据好友id列表查询好友的详细信息
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);
        //Page<UserInfo> pageInfo = userInfoApi.findByIdsAndPage(friendIds, userInfo, page, pagesize);
        //List<UserInfo> records = pageInfo.getRecords();
        //List<ContactVo> vos = records.stream().map(ContactVo::init).collect(Collectors.toList());
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(friendIds, userInfo).stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
        List<ContactVo> vos = friendIds.stream()
                .filter(id -> userInfoMap.get(id) != null)
                .map(id -> ContactVo.init(userInfoMap.get(id)))
                .collect(Collectors.toList());
        // 返回结果
        return new PageResult(page, pagesize, 0L, vos);
    }
}
