package com.petgoorm.backend.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //4번째에 있는 숫자 에러코드 말고 string 으로 된 2번쨰 에러코드를 사용하고 싶었지만
    //기존에 클라이언트로 보내는 모든 에러코드가 int형식이라 우선은 4번째 숫자 에러코드를 클라이언트에 반환함
    //리팩토링 할때 string 형식으로 고치거나 숫자 에러코드를 계속 사용하면 될 것 같음

    // Member
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST,"MEMBER-0001", "Member not found",1001),
    EXISTING_MEMBER(HttpStatus.BAD_REQUEST, "MEMBER-0002", "Member that already exists",1002),

    // Post
    NOT_FOUND_POST(HttpStatus.BAD_REQUEST, "POST-0001", "Post not found",2001),

    // File
    AWS_S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "FILE-0001", "AWS S3 File upload fail",3001),
    AWS_S3_UPLOAD_VALID(HttpStatus.BAD_REQUEST, "FILE-0002", "File validation",3002),

    // Role
    NOT_HAVE_PERMISSION(HttpStatus.BAD_REQUEST, "ROLE-0001", "You do not have permission",4001),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ROLE-0002", "Unauthorized",4002),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ROLE-0003", "Forbidden",4003),

    //JWT
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0001", "Access token has expired",5001),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "Refresh token has expired",5002);

    HttpStatus status;
    String code;
    String message;
    Integer codeNum;


}
