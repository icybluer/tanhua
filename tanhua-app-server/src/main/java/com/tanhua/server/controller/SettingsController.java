package com.tanhua.server.controller;

import com.tanhua.model.domain.dto.QuestionDto;
import com.tanhua.model.domain.dto.SettingsDto;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.domain.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    /**
     * 用户通用设置 - 读取
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 设置陌生人问题 - 保存
     */
    @PostMapping("/question")
    public ResponseEntity setQuestion(@RequestBody QuestionDto dto) {
        settingsService.setQuestion(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 通知设置 - 保存
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity setNotifications(@RequestBody SettingsDto dto) {
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
