package com.petgoorm.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.ResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//인증이 되지않은 사용자가 접근시 에러(security 필터단에서 발생한 인증 에러 가로채 핸들링)
@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException, ServletException {
        String exception = (String) httpServletRequest.getAttribute("exception");
        ResponseDTO responseDTO = ResponseDTO.of(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED",null);

        String result = objectMapper.writeValueAsString(responseDTO);
        log.info("CustomAuthenticationEntryPoint: 인증 예외 발생");
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.getWriter().write(result);
        httpServletResponse.getWriter().flush();
    }
}