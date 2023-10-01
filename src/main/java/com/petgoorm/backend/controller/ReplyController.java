package com.petgoorm.backend.controller;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/reply")
public class ReplyController {

    private final ReplyService replyService;

    // 댓글 등록
    @PostMapping("/create")
    public ResponseDTO<Long> create (@RequestBody BoardRequestDTO.Reply replyDTO) {
        log.info("replyDTO: "+ replyDTO);
        return replyService.create(replyDTO);
    }

    @GetMapping("/list/{boardId}")
    public ResponseDTO<List<BoardResponseDTO.Reply>> replyList(@PathVariable Long boardId){
        return replyService.replyList(boardId);
    }

    @PutMapping("/update/{rId}")
    public ResponseDTO<Long> update(@RequestBody BoardRequestDTO.Reply replyDTO, @PathVariable Long rId){
        log.info("업데이트 "+ replyDTO);
        return replyService.update(replyDTO, rId);
    }

    @DeleteMapping("/delete/{rId}")
    public ResponseDTO<Long> delete(@PathVariable Long rId){
        log.info("rID 확인: "+rId);
        return replyService.delete(rId);
    }

}
