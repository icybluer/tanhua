package com.tanhua.server.controller;

import com.tanhua.model.dto.QuestionDTO;
import com.tanhua.model.dto.SettingsDTO;
import com.tanhua.model.dto.UserInfoDTO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVO;
import com.tanhua.model.vo.UserInfoVO;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户资料读取
     */
    @GetMapping
    public ResponseEntity userInfo(Long id) {
        //2. 根据id查询用户详细信息，若id为空，则查询当前用户
        id = id == null ? UserHolder.getUserId() : id;
        UserInfoVO vo = userInfoService.findById(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * 修改用户资料
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfoDTO dto) {
        //从token获取用户id，根据id修改用户详细信息
        Long id = UserHolder.getUserId();
        dto.setId(id);
        userInfoService.updateUserInfo(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 用户通用设置 - 读取
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVO vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 设置陌生人问题 - 保存
     */
    @PostMapping("/question")
    public ResponseEntity setQuestion(@RequestBody QuestionDTO dto) {
        settingsService.setQuestion(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 通知设置 - 保存
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity setNotifications(@RequestBody SettingsDTO dto) {
        settingsService.setNotifications(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 黑名单 - 翻页列表
     */
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PageResult pageResult = settingsService.blacklist(page, size);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 黑名单 - 移除
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity removeBlackList(@PathVariable("uid") Long uid) {
        settingsService.removeBlackList(uid);
        return ResponseEntity.ok(null);
    }
}
