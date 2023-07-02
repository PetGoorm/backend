package com.petgoorm.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.pet.PetDTO;
import com.petgoorm.backend.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PetTests {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void nowEmail(){
        // 가상의 사용자 이메일 값
        String userEmail = "test@naver.com";

        // 가상의 인증 객체 생성
        Authentication authentication = new TestingAuthenticationToken(userEmail, null);

        // SecurityContextHolder에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);


        System.out.println(SecurityUtil.getCurrentUserEmail());
    }

    @Test
    @DisplayName("펫 수정 컨트롤러 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void update() throws Exception {

        PetDTO petDTO = PetDTO.builder()
                .petname("하이")
                .build();

        mockMvc.perform(put("/pet/edit/{petId}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)
                                ))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    @DisplayName("펫 삭제 컨트롤러 테스트")
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void delete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/pet/remove/{petId}", 2))
                .andExpect(status().isOk())
                .andDo(print());


    }




}
