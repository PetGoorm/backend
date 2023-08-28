package com.petgoorm.backend.controller;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    // 글 목록 조회
    @GetMapping("/list")
    public ResponseDTO<List<BoardResponseDTO>> getBoardList(@RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.getRegionBoardList(tokenWithoutBearer);
    }

    // 글 조회
    @GetMapping("/{boardId}")
    public ResponseDTO<BoardResponseDTO> getBoard(@PathVariable Long boardId) {
        return boardService.getOneBoard(boardId);
    }

    // 글 등록
    @PostMapping("/create")
    public ResponseDTO<Long> create (@RequestBody BoardRequestDTO boardRequestDTO, @RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.create(boardRequestDTO,tokenWithoutBearer);
    }

    // 글 삭제
    @DeleteMapping ("/delete/{boardId}")
    public ResponseDTO<String> delete (@PathVariable Long boardId, @RequestHeader("Authorization") String accessToken){
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.delete(boardId,tokenWithoutBearer);
    }

    // 글 수정
    @PutMapping("/edit/{boardId}")
    public ResponseDTO<Long> edit(@PathVariable Long boardId, @Valid @RequestBody BoardRequestDTO.edit EditForm,@RequestHeader("Authorization") String accessToken) {
        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        return boardService.updatePut(boardId, EditForm, tokenWithoutBearer);
    }


}
