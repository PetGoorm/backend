package com.petgoorm.backend.service;

import com.petgoorm.backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class RedisBoardServiceImpl implements RedisBoardService {

    private final BoardRepository boardRepository;

    private final String VIEW_COUNTS_KEY = "boardViewCnt"; // 조회수 해시 키 (안에 필드(boardId)로 한번 더 분류)
    private static final String USER_VIEW_RECORDS_KEY = "BoardViewDuplicateCheck::memberId::"; // 사용자 중복 조회 키 (멤버별 생성)
    private static final String EXPIRATION_KEY = "BoardViewExpirationTime"; //조회수 만료 (안에 필드(boardId)로 한번 더 분류)

    private RedisTemplate<String, String> redisTemplate; // 문자열로 처리
    private HashOperations<String, String, String> hashOps;
    private ZSetOperations<String, String> zSetOps;
    private SetOperations<String, String> opsForSet;

    @Autowired
    public void ViewCountService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
        this.opsForSet = redisTemplate.opsForSet();
        this.hashOps = redisTemplate.opsForHash();
    }

    /*
    게시물 조회수 저장 및 조회 메서드 - Redis에 캐싱 후 스케줄러로 DB에 반영
    레디스 조회수 저장 방식 - HashOperations<String, String, String> / 조회 명령어 - hget boardViewCnt boardId:{boardId}
    레디스 조회수 만료 시간 저장 방식 - ZSetOperations<String, String> / 조회 명령어 - zscore BoardViewExpirationTime boardId:{boardId}
     */
    public Long RedisGetOrIncementBoardViewCount(Long boardId){
        String field = "boardId:" + boardId;
        String viewCnt = hashOps.get(VIEW_COUNTS_KEY, field);

        // Redis에서 조회한 값이 null 또는 "null" 문자열인 경우, DB에서 해당 게시물 조회수를 가져와 캐싱
        if (viewCnt == null || viewCnt.equals("null")) {
            Long DBCnt = boardRepository.findClickCntByBoardId(boardId);
            hashOps.put(VIEW_COUNTS_KEY, field, String.valueOf(DBCnt));
        }
        //조회할 때 마다 조회수 +1
        Long newCnt = hashOps.increment(VIEW_COUNTS_KEY, field, 1);

        // 캐싱 만료 시간 설정 (1일)
        Long currentUnixTimestamp = System.currentTimeMillis();
        Long expirationTimestamp = currentUnixTimestamp + (24L * 60L * 60L * 1000L);
        zSetOps.add(EXPIRATION_KEY, field, expirationTimestamp);

        return newCnt;
    }







    /*
    게시물 중복 조회 판단 메서드 - 판단 기준: 유저, 캐시 만료는 24시간 이후
    레디스 저장 방식 - SetOperations<String,String>
    터미널 조회 명령어 - SMEMBERS BoardViewRecords::{memberId}
    */
    public boolean isDuplicateView(Long memberId, Long boardId) {
        String userRecordKey = USER_VIEW_RECORDS_KEY + memberId;

        // 레디스 Set에 사용자의 조회 기록이 있는지 확인
        Boolean isDuplicate = opsForSet.isMember(userRecordKey, boardId.toString());

        if (isDuplicate != null && isDuplicate) {
            // 중복 조회인 경우
            return true;
        } else {
            // 중복 조회가 아닌 경우, 조회 기록을 저장
            redisTemplate.opsForSet().add(userRecordKey, boardId.toString());
            redisTemplate.expire(userRecordKey, 24, TimeUnit.HOURS);
            return false;
        }
    }
}