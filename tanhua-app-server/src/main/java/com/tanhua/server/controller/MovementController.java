package com.tanhua.server.controller;

import com.tanhua.model.domain.dto.MovementDto;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.mongo.Movement;
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

    @PostMapping
    public ResponseEntity movements(MovementDto dto, MultipartFile imageContent[]) throws IOException {
        movementService.publishMovement(dto, imageContent);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/all")
    public ResponseEntity findAll(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pagesize,
                                  Long userId) {
        PageResult pageInfo = movementService.findAll(userId, page, pagesize);
        return ResponseEntity.ok(pageInfo);
    }
}
