package com.tanhua.server.controller;

import com.tanhua.model.dto.CommentDTO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 查询全部评论
     */
    @GetMapping
    public ResponseEntity getComments(
            @RequestParam String movementId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize
    ) {
        PageResult pageInfo = commentService.getComments(movementId, page, pagesize);
        return ResponseEntity.ok(pageInfo);
    }

    /**
     * 发布评论
     */
    @PostMapping
    public ResponseEntity publishComment(@RequestBody CommentDTO dto) {
        Integer commentCount = commentService.publishComment(dto);
        return ResponseEntity.ok(commentCount);
    }
}
