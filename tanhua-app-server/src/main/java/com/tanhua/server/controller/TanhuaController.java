package com.tanhua.server.controller;

import com.tanhua.model.domain.dto.RecommendUserDto;
import com.tanhua.model.domain.vo.PageResult;
import com.tanhua.model.domain.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {
    @Autowired
    private TanhuaService tanhuaService;

    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        TodayBest vo = tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto dto) {
        PageResult vo = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(vo);
    }
}