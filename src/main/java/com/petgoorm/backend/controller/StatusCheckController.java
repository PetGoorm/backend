package com.petgoorm.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StatusCheckController {

    @GetMapping("/health")
    public ResponseEntity<Void> checkHealthStatus() {
        log.info("로드밸런서 /health 경로 접속");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
