package com.tanhua.server.controller;

import com.tanhua.model.dto.UserDTO;
import com.tanhua.model.dto.UserInfoDTO;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVO;
import com.tanhua.model.vo.UserVO;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录-发送验证码
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDTO dto) {
        try {
            //1. 获取手机号
            String phone = dto.getPhone();
            //2. 发送验证码
            userService.sendCode(phone);
            //3. 返回数据
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            throw new BusinessException(ErrorResult.fail());
        }
    }

    /**
     * 校验登录
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody UserDTO dto) {
        //1 调用userService完成用户登录
        UserVO userVo = userService.loginVerification(dto.getPhone(), dto.getVerificationCode());
        //2 构造返回
        return ResponseEntity.ok(userVo);
    }

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 首次登录完善资料
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfoDTO dto) {
        //1. 从token获取当前用户id
        Long id = UserHolder.getUserId();
        //2. 保存用户信息到数据库
        dto.setId(id);
        userInfoService.save(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 上传保存用户头像
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) {
        //1. 获取当前用户id
        Long id = UserHolder.getUserId();
        //2. 保存头像地址到数据库
        try {
            userInfoService.updateAvatar(headPhoto, id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(null);
    }

}