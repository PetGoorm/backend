package com.petgoorm.backend.service;

import org.springframework.stereotype.Service;

@Service
public interface RedisBoardService {

    public Long RedisGetOrIncementBoardViewCount(Long boardId);

    public boolean isDuplicateView(Long memberId, Long boardId);

}