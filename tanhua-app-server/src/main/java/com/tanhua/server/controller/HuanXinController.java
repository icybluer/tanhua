package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.service.HuanXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {
    @Autowired
    private HuanXinService huanXinService;

    @GetMapping("/user")
    private ResponseEntity user() {
        HuanXinUserVo vo = huanXinService.getHuanXinUser();
        return ResponseEntity.ok(vo);
    }
}