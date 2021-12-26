package com.tanhua.server.service;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.dto.MovementDto;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.ErrorResult;
import com.tanhua.model.domain.vo.MovementsVo;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.mongo.Movement;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService {
    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    public void publishMovement(MovementDto dto, MultipartFile[] imageContent) throws IOException {
        // 校验动态内容是否为空
        if (dto.getTextContent() == null) {
            throw new BusinessException(ErrorResult.contentError());
        }
        Movement movement = new Movement();
        // 获取当前用户id
        Long userId = UserHolder.getUserId();
        movement.setUserId(userId);
        // 上传媒体对象到oss
        if (imageContent != null) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : imageContent) {
                String url = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
                urls.add(url);
            }
            movement.setMedias(urls);
        }
        // 封装到movement对象
        movement.setTextContent(dto.getTextContent());
        movement.setLocationName(dto.getLocation());
        movement.setLongitude(dto.getLongitude());
        movement.setLatitude(dto.getLatitude());
        // 保存到movement表
        movementApi.publish(movement);
    }

    public PageResult findAll(Long userId, Integer page, Integer pagesize) {
        PageResult pageInfo = movementApi.findByUserId(userId, page, pagesize);
        List<Movement> items = (List<Movement>) pageInfo.getItems();
        if (items == null || items.size() == 0) {
            return pageInfo;
        }
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> vos = items.stream()
                .map(movement -> MovementsVo.init(userInfo, movement))
                .collect(Collectors.toList());
        pageInfo.setItems(vos);
        return pageInfo;
    }
}
