package com.petgoorm.backend.handler;

import com.petgoorm.backend.handler.ErrorCode;
import lombok.Getter;


@Getter
//어떤 에런지 파라미터로 받으면서 기본생성자에 등록
public class CustomException extends RuntimeException {
    private ErrorCode error;

    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
}