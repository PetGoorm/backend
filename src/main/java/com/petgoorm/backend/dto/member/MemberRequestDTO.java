package com.petgoorm.backend.dto.member;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


public class MemberRequestDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUp {
        private String email;

        private String password;

        private String memberName;

        private String nickname;

        private String phoneNumber;

        private String provider;

        private String snsId;

        private String memberPic;

        private String address;

        private String bcode;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Login {
        private String email;
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdatePassword{
        private String nowPW;
        private String updatePW;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateNick{
        private String nickname;
    }

}
