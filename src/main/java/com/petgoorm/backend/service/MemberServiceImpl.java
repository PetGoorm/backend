package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.member.MemberRequestDTO;
import com.petgoorm.backend.dto.member.MemberResponseDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.jwt.JwtTokenProvider;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.repository.PetRepository;
import com.petgoorm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

    //회원등록
    @Override
    @Transactional
    public ResponseDTO<Long> signup(MemberRequestDTO.SignUp memberRequestDTO) {
        if (memberRepository.existsByEmail(memberRequestDTO.getEmail())) {
            return ResponseDTO.of(HttpStatus.CONFLICT.value(),"이미 가입되어 있는 유저입니다",null);
        }
        Member member = toEntity(passwordEncoder, memberRequestDTO);
        memberRepository.save(member);
        return ResponseDTO.of(HttpStatus.OK.value(),"회원가입이 성공했습니다.",member.getId());

    }

    //이메일 중복체크
    @Override
    @Transactional(readOnly = true)
    public ResponseDTO<String> checkEmailDuplication(String email){
        boolean emailDuplicate = memberRepository.existsByEmail(email);
        log.info("중복체크 이메일: " + emailDuplicate);
        try {
            if (emailDuplicate) {
                //중복
                return ResponseDTO.of(HttpStatus.CONFLICT.value(), "이미 가입되어 있는 유저입니다", null);
            } else {
                //사용가능한 이메일
                log.info("사용 가능한 이메일: " + email);
                return ResponseDTO.of(HttpStatus.OK.value(), "사용 가능한 이메일입니다.", email);
            }
        }catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }


    //닉네임 중복체크
    @Override
    @Transactional(readOnly = true)
    public ResponseDTO<String> checkNicknameDuplication(String nickname) {
        boolean nicknameDuplicate = memberRepository.existsByNickname(nickname);
        try {
            if (nicknameDuplicate) {
                //중복
                log.info("중복 닉네임: "+nickname);
                return ResponseDTO.of(HttpStatus.CONFLICT.value(), "이미 존재하는 닉네임입니다.", null);
            } else {
                //사용가능한 이메일
                log.info("사용 가능한 닉네임: "+nickname);
                return ResponseDTO.of(HttpStatus.OK.value(), "사용 가능한 닉네임입니다.", nickname);
            }
        }catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO<MemberResponseDTO.TokenInfo> login(MemberRequestDTO.Login login) {

        //이메일이 db에 존재하지 않을때
        if (memberRepository.findByEmail(login.getEmail()).orElse(null) == null) {
            return ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), "해당하는 유저가 존재하지 않습니다.", null);

        }

        try {
            // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
            // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
            UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();
            log.info("1. authenticationToken : "+authenticationToken);
            // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("2. authentication : "+authentication);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            MemberResponseDTO.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            log.info("3. tokenInfo : "+tokenInfo);

            // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
            // getName = 사용자의 이메일
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            log.info("4. redis 저장");
            return ResponseDTO.of(HttpStatus.OK.value(), "로그인에 성공했습니다.", tokenInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }

    //refresh token 유효성 검증 후 access token 재발급
    @Override
    public ResponseDTO<String> reissue(String nowAccessToken) {
        // 1. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(nowAccessToken);

        // 2. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // 3. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Refresh Token 정보가 유효하지 않습니다.", null);
        }

        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 요청입니다", null);
        }

        // 4. 새로운 access 토큰 생성
        String accessToken = jwtTokenProvider.accessToken(authentication);

        return ResponseDTO.of(HttpStatus.OK.value(), "Token 정보가 갱신되었습니다.", accessToken);
    }



    @Transactional
    @Override
    public ResponseDTO<String> logout(String accessToken) {

        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.", null);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        // 현재 redis에 저장되어 있는건 refreshtoken이고 이걸 삭제한다고 해도
        // accesstoken은 만료되지 않는 한 살아있기 때문에 탈취당하면 안전하지 않다.
        // 따라서 로그아웃 요청이 올 경우 이를 redis에 블랙리스트로 등록하여
        // 요청에 포함된 accesstoken이 블랙리스트에 있을경우 거부하게 한다.
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        return ResponseDTO.of(HttpStatus.OK.value(), "로그아웃 성공했습니다.", authentication.getName());
    }

    //비밀번호 변경 서비스
    @Transactional
    @Override
    public ResponseDTO<Long> updatePassword(MemberRequestDTO.UpdatePassword updatePassword){
        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try{
            boolean pwcheck = passwordEncoder.matches(updatePassword.getNowPW(), member.getPassword());

            if(pwcheck){
                member.updatePassword(passwordEncoder.encode(updatePassword.getUpdatePW()));
                return ResponseDTO.of(HttpStatus.OK.value(), "비밀번호 변경에 성공했습니다.", member.getId());
            }
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호가 올바르지 않습니다", null);

        }
        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }


    }


    //닉네임 변경 서비스
    @Transactional
    @Override
    public ResponseDTO<Long> updateNick(MemberRequestDTO.UpdateNick updateNick){
        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try{
                member.updateNick(updateNick.getNickname());
                return ResponseDTO.of(HttpStatus.OK.value(), "닉네임 변경에 성공했습니다.", member.getId());
        }

        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }

    //회원 탈퇴 서비스
    @Transactional
    @Override
    public ResponseDTO<Long> deleteMember(){
        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try{
            petRepository.deletePetByMemberId(member.getId());
            memberRepository.deleteMemberByEmail(member.getEmail());
            return ResponseDTO.of(HttpStatus.OK.value(), "회원 탈퇴에 성공했습니다.", member.getId());
        }

        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }


}
