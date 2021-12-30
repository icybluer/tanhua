package com.tanhua.server.controller;

import com.tanhua.model.dto.QuestionDTO;
import com.tanhua.model.dto.RecommendUserDTO;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity recommendation(RecommendUserDTO dto) {
        PageResult vo = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long id) {
        TodayBest vo = tanhuaService.getPersonalInfo(id);
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(@RequestParam("userId") Long userId) {
        String question = tanhuaService.getStrangerQuestion(userId);
        return ResponseEntity.ok(question);
    }

    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestion(@RequestBody QuestionDTO dto) {
        tanhuaService.replyQuestion(dto);
        return ResponseEntity.ok(null);
    }
}
