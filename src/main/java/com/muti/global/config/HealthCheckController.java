package com.muti.global.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 무중단 배포 시 로드밸런서 health check를 위한 간단한 엔드포인트
 * Actuator의 /actuator/health와 별도로 운영
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}