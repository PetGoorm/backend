package com.petgoorm.backend.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.ResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//권한이 없는 사용자가 접근할 시 에러(인가 에러- security 필터 단)
@Component
@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e)
            throws IOException, ServletException {
        ResponseDTO responseDTO = ResponseDTO.of(HttpStatus.FORBIDDEN.value(), "Forbidden",null);

        String result = objectMapper.writeValueAsString(responseDTO);
        log.info("CustomAuthenticationEntryPoint: 인가 예외 발생");
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.getWriter().write(result);
        httpServletResponse.getWriter().flush();
    }
}