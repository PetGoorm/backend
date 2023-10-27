package com.petgoorm.backend.handler;

import com.petgoorm.backend.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 컨트롤러에서 발생한 exception에러를 가로채 handling하는 역할의 어노테이션
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({CustomException.class})
    public ResponseEntity<ResponseDTO> customExceptionHandler(CustomException e) {
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(
                        ResponseDTO.builder()
                                .statusCode(e.getError().getCodeNum())
                                .message(e.getError().getMessage())
                                .build()
                );
    }
}
