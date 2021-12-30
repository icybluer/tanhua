package com.tanhua.server.controller;

import com.tanhua.model.dto.ContactDTO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVO;
import com.tanhua.server.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    @RequestMapping("/userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        UserInfoVO vo = messagesService.getUserInfoByHuanXinId(huanxinId);
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody ContactDTO dto) {
        messagesService.contacts(dto.getUserId());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/contacts")
    public ResponseEntity getFriends(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pagesize,
                                     String keyword) {
        PageResult pageInfo = messagesService.getFriends(page, pagesize, keyword);
        return ResponseEntity.ok(pageInfo);
    }
}
