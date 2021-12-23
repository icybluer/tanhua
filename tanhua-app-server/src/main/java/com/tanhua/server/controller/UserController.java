package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.dto.UserDto;
import com.tanhua.model.domain.pojo.UserInfo;
import com.tanhua.model.domain.vo.UserVo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录-发送验证码
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto dto) {
        //1. 获取手机号
        String phone = dto.getPhone();
        //2. 发送验证码
        userService.sendCode(phone);
        //3. 返回数据
        return ResponseEntity.ok(null);
    }

    /**
     * 校验登录
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody UserDto dto) {
        //1 调用userService完成用户登录
        UserVo userVo = userService.loginVerification(dto.getPhone(), dto.getVerificationCode());
        //2 构造返回
        return ResponseEntity.ok(userVo);
    }

    /**
     * 首次登录完善资料
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String token) {
        System.out.println(token);
        //1. 校验token是否合法
        if (!JwtUtils.verifyToken(token)) {
            throw new RuntimeException();
        }

        //2. 合法则保存用户信息到数据库

        return ResponseEntity.ok(null);

    }
}