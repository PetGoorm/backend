package com.petgoorm.backend.dto;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
//of를 사용하여 값을 집어넣으니까 코드를 한눈에 파악하기 어려워서 빌더 패턴도 추가함
@Builder
@RequiredArgsConstructor(staticName = "of")
//공통 응답 DTO
public class ResponseDTO<T> {
    private final int statusCode;
    private final String message;
    private final T data;
}
