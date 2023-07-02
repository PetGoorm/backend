package com.petgoorm.backend.controller;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.member.MemberRequestDTO;
import com.petgoorm.backend.dto.member.MemberResponseDTO;
import com.petgoorm.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;

    //회원추가
    @PostMapping("/signup")
    public ResponseDTO<Long> signup(@RequestBody MemberRequestDTO.SignUp memberRequestDTO) {
        //System.out.println(memberRequestDTO);
        return memberService.signup(memberRequestDTO);
    }

    //이메일 중복체크
    @GetMapping("/checkEmail")
    public ResponseDTO<String> checkEmail(@RequestParam String email) {
        log.info("이메일 " + email);
        return memberService.checkEmailDuplication(email);
    }

    //닉네임 중복체크
    @GetMapping("/checkNick")
    public ResponseDTO<String> checkNick(@RequestParam String nickname) {
        return memberService.checkNicknameDuplication(nickname);
    }

    @PostMapping("/login")
    public ResponseDTO<MemberResponseDTO.TokenInfo> login(@RequestBody MemberRequestDTO.Login login) {
        return memberService.login(login);
    }

    @PostMapping("/reissue")
    public ResponseDTO<String> reissue(@RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return memberService.reissue(tokenWithoutBearer);
    }

    @PostMapping("/logout")
    public ResponseDTO<String> logout(@RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return memberService.logout(tokenWithoutBearer);
    }

    @PatchMapping("/editpw")
    public ResponseDTO<Long> updatePassword(@RequestBody MemberRequestDTO.UpdatePassword updatePassword){
        return memberService.updatePassword(updatePassword);

    }
    @PatchMapping("/editnick")
    public ResponseDTO<Long> updateNick(@RequestBody MemberRequestDTO.UpdateNick updateNick){
        return memberService.updateNick(updateNick);

    }
    @DeleteMapping("/remove")
    public ResponseDTO<Long> delete(){
        return memberService.deleteMember();
    }





}
