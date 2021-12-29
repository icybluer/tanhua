package com.tanhua.server.controller;

import com.tanhua.model.dto.MovementDTO;
import com.tanhua.model.vo.MovementsVO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MovementController {
    @Autowired
    private MovementService movementService;

    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity movements(MovementDTO dto, MultipartFile imageContent[]) throws IOException {
        movementService.publishMovement(dto, imageContent);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询我的动态
     */
    @GetMapping("/all")
    public ResponseEntity findAll(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pagesize,
        Long userId) {
        PageResult pageInfo = movementService.findAll(userId, page, pagesize);
        return ResponseEntity.ok(pageInfo);
    }

    /**
     * 查询好友动态
     */
    @GetMapping
    public ResponseEntity findFriendsMovements(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pagesize){
        PageResult pageInfo = movementService.findFriendsMovements(page, pagesize);
        return ResponseEntity.ok(pageInfo);
    }

    /**
     * 查询推荐动态
     */
    @GetMapping("/recommend")
    public ResponseEntity getRecommendMovements(@RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pagesize){
        PageResult pageInfo = movementService.getRecommendMovements(page, pagesize);
        return ResponseEntity.ok(pageInfo);
    }

    /**
     * 查询单条动态
     */
    @GetMapping("/{id}")
    public ResponseEntity getSimpleMovement(@PathVariable("id") String id) {
        MovementsVO vo = movementService.findById(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * 动态的点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity likeComment(@PathVariable("id") String movementId) {
        Integer likeCount = movementService.likeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislikeComment(@PathVariable("id") String movementId) {
        Integer likeCount = movementService.dislikeComment(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 动态-喜欢
     */
    @GetMapping("/{id}/love")
    public ResponseEntity loveComment(@PathVariable("id") String movementId) {
        Integer loveCount = movementService.loveComment(movementId);
        return ResponseEntity.ok(loveCount);
    }

    /**
     * 动态-取消喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unloveComment(@PathVariable("id") String movementId) {
        Integer likeCount = movementService.unloveComment(movementId);
        return ResponseEntity.ok(likeCount);
    }
}
