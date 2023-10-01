package com.petgoorm.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Reply;
import com.petgoorm.backend.repository.ReplyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
public class ReplyTests {

    @Autowired
    private ReplyRepository replyRepository;


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Transactional
    @DisplayName("댓글 목록 조회 레포지토리 테스트")
    public void testListByBoard() {
        // 특정 게시판을 나타내는 Board 객체 생성
        Board board = Board.builder()
                .boardId(1L)
                .build();

        List<Reply> replyList = replyRepository.findByBoard(board);
        replyList.forEach(reply -> System.out.println(reply));

    }

    @Test
    @DisplayName("댓글 등록 컨트롤러 테스트")
    @WithMockUser(username = "seoul2@naver.com", roles = {"USER"})
    public void create() throws Exception {

        BoardRequestDTO.Reply replyDTO = BoardRequestDTO.Reply.builder()
                .boardId(1L)
                .content("이것은 댓글2")
                .build();

        mockMvc.perform(post("/api/reply/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyDTO)
                        ))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 목록 컨트롤러 조회 레포지토리 테스트")
    @WithMockUser(username = "seoul2@naver.com", roles = {"USER"})
    public void testReplyList() throws Exception {

        mockMvc.perform(get("/api/reply/list/{boardId}",1L))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 컨트롤러 테스트")
    @WithMockUser(username = "seoungnam@naver.com", roles = {"USER"})
    public void update() throws Exception {

        BoardRequestDTO.Reply replyDTO = BoardRequestDTO.Reply.builder()
                .boardId(1L)
                .content("수정했습니다")
                .build();

        mockMvc.perform(put("/api/reply/update/{rId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyDTO)
                        ))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("댓글 삭제 컨트롤러 테스트")
    @WithMockUser(username = "seoungnam@naver.com", roles = {"USER"})
    public void delete() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reply/delete/{rId}",1))
                .andExpect(status().isOk())
                .andDo(print());
    }





}
