package com.petgoorm.backend.controller;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.service.BoardService;
import com.petgoorm.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    // 글 등록
    @PostMapping("/create")
    public ResponseDTO<Long> create (@RequestBody BoardRequestDTO boardRequestDTO, @RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.create(boardRequestDTO,tokenWithoutBearer);
    }

    //글 조회
    @GetMapping("/{boardId}")
    public ResponseDTO<BoardResponseDTO> getBoard(@PathVariable Long boardId) {
        return boardService.findOneBoard(boardId);
    }

    // 글 삭제
    @DeleteMapping ("/delete/{boardId}")
    public ResponseDTO<String> delete (@PathVariable Long boardId, @RequestHeader("Authorization") String accessToken){
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.delete(boardId,tokenWithoutBearer);
    }

    // 글 수정
    @GetMapping("/{boardId}/edit")
    public ResponseDTO<BoardResponseDTO.edit> editForm(@PathVariable Long boardId, @RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.updateGet(boardId,tokenWithoutBearer);
    }

    @PutMapping("/{boardId}/edit")
    public ResponseDTO<Long> edit(@PathVariable Long boardId, @Valid @RequestBody BoardRequestDTO.edit EditForm, BindingResult bindingResult, @RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.updatePut(boardId, EditForm, tokenWithoutBearer);
    }


}
