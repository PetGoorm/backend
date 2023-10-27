package com.petgoorm.backend.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.handler.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            String token = resolveToken(request);
            String path = request.getServletPath();
            log.info(path);
            if (path.startsWith("/member/reissue")) {
                chain.doFilter(request, response);
            } else{
                    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                        // (추가) Redis 에 해당 accessToken logout 여부 확인
                        String isLogout = (String) redisTemplate.opsForValue().get(token);

                        if (ObjectUtils.isEmpty(isLogout)) {
                            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                            Authentication authentication = jwtTokenProvider.getAuthentication(token);
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                        }
                    }

                    chain.doFilter(request, response);
                }

        } catch (ExpiredJwtException e) {
            //토큰의 유효기간 만료시 5001 에러코드 클라이언트에 반환
            setErrorResponse(response, ErrorCode.JWT_ACCESS_TOKEN_EXPIRED);
        }

    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //filter에서 발생하는 예외는 restcontrolleradvice로 잡을 수 없기때문에 따로 메서드를 생성함
    //에러코드 커스텀 가능
    //토큰 유효기간 만료 외의 인증인가 에러는
    //customaccesssdenidedhandler와 customauthenticationentrypoint클래스에서 핸들링됨
    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) {

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // UTF-8로 인코딩 설정

        ResponseDTO<?> responseDTO = ResponseDTO.of(errorCode.getCodeNum(), errorCode.getMessage(), null);

        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDTO));
            response.getWriter().flush();

            log.info("setErrorResponse: " + responseDTO.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}