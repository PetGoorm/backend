package com.petgoorm.backend.service;


import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.member.MemberRequestDTO;
import com.petgoorm.backend.dto.member.MemberResponseDTO;
import com.petgoorm.backend.entity.Authority;
import com.petgoorm.backend.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

public interface MemberService {

    //DTO->Entity 변환 메서드
    default Member toEntity(PasswordEncoder passwordEncoder, MemberRequestDTO.SignUp requestDTO) {
        Member member = Member.builder()
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .memberName(requestDTO.getMemberName())
                .nickname(requestDTO.getNickname())
                .phoneNumber(requestDTO.getPhoneNumber())
                .provider("local")
                .snsId(requestDTO.getSnsId())
                .memberPic(requestDTO.getMemberPic())
                .address(requestDTO.getAddress())
                .bcode(requestDTO.getBcode())
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        return member;
    }

    //멤버 정보 반환 DTO로 변환
    default MemberRequestDTO.SignUp toDTO(Member member){
        MemberRequestDTO.SignUp memberDTO = MemberRequestDTO.SignUp.builder()
                .memberName(member.getMemberName())
                .email(member.getEmail())
                .address(member.getAddress())
                .phoneNumber(member.getPhoneNumber())
                .bcode(member.getBcode())
                .nickname(member.getNickname())
                .build();
        return memberDTO;
    }




    //회원등록
    ResponseDTO<Long> signup(MemberRequestDTO.SignUp memberRequestDTO);

    //이메일 중복체크
    ResponseDTO<String> checkEmailDuplication(String email);

    //닉네임 중복체크
    ResponseDTO<String> checkNicknameDuplication(String nickname);

    ResponseDTO<MemberResponseDTO.TokenInfo> login(MemberRequestDTO.Login login);

    //refresh token 유효성 검증 후 access token 재발급
    ResponseDTO<String> reissue(String nowAccessToken);

    ResponseDTO<String> logout(String accessToken);

    ResponseDTO<Long> updatePassword(MemberRequestDTO.UpdatePassword updatePassword);

    ResponseDTO<Long> updateNick(MemberRequestDTO.UpdateNick updateNick);

    //회원 정보 반환(마이페이지)
    @Transactional
    ResponseDTO<MemberRequestDTO.SignUp> memberInfo();

    ResponseDTO<Long> deleteMember();


    //회원정보 업데이트(마이페이지) 서비스
    @Transactional
    ResponseDTO<Long> updateMember(MemberRequestDTO.SignUp updateInfo);
}
