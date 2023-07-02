package com.petgoorm.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.member.MemberRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Test
    @DisplayName("비밀번호 변경 컨트롤러 테스트")
    @WithUserDetails(value="test0524@naver.com")
    public void updatePassword() throws Exception {

        MemberRequestDTO.UpdatePassword updatePasswordDTO = MemberRequestDTO.UpdatePassword.builder()
                .nowPW("qwer1234")
                .updatePW("qqqq1111")
                .build();

        mockMvc.perform(patch("/member/editpw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDTO)
                        ))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 변경 컨트롤러 테스트")
    @WithUserDetails(value="test0524@naver.com")
    public void updateNick() throws Exception {

        MemberRequestDTO.UpdateNick updateNick = MemberRequestDTO.UpdateNick.builder().nickname("테스트0627").build();

        mockMvc.perform(patch("/member/editnick")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNick)
                        ))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @DisplayName("회원 삭제 컨트롤러 테스트")
    @WithMockUser(username = "bys@naver.com", roles = {"USER"})
    public void delete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/member/remove"))
                .andExpect(status().isOk())
                .andDo(print());


    }





}
