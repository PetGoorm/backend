package com.petgoorm.backend.config;

import com.petgoorm.backend.service.RedisBoardServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;

@Component
@Log4j2
public class RedisViewCountScheduler {

    private final RedisBoardServiceImpl redisBoardService;

    @Autowired
    public RedisViewCountScheduler(RedisBoardServiceImpl redisBoardService) {
        this.redisBoardService = redisBoardService;
    }

    // 1시간(60분)마다 스케줄러 실행!
    @Scheduled(fixedRate = 24L * 60L * 60L * 1000L)
    @Transactional
    public void removeExpiredViewCounts() {
        // 현재 시간 기준으로 만료된 조회수 정보 확인 및 삭제
        log.info("현재 시간 기준, 하루 지난 조회수 캐시 및 관련 정보를 삭제하는 스케쥴러를 실행합니다.");
        redisBoardService.removeExpiredViewCounts();

    }

    // 3분마다 실행 스케줄러 실행!
    @Scheduled(fixedRate = 180000)
    public void updateRedisViewCountsToDB(){
        log.info("캐시 조회수와 DB 조회수 데이터를 병합하는 스케쥴러를 실행합니다.");
        redisBoardService.updateRedisViewCountsToDB();

    }
}