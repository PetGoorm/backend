package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.Reply;

import java.util.ArrayList;
import java.util.List;

public interface ReplyService {


    //DTO->Entity 변환 메서드
    default Reply toEntity(BoardRequestDTO.Reply replyDTO, Member member, Board board) {
        Reply reply = Reply.builder()
                .content(replyDTO.getContent())
                .writer(member)
                .board(board)
                .build();
        return reply;
    }

    default BoardResponseDTO.Reply toDTO(Reply reply) {
        BoardResponseDTO.Reply replyDTO = BoardResponseDTO.Reply.builder()
                .rId(reply.getRId())
                .content(reply.getContent())
                .moddate(reply.getModDate())
                .regdate(reply.getRegDate())
                .writerNickname(reply.getWriter().getNickname())
                .build();
        return replyDTO;
    }


    //댓글 목록 조회
    default List<BoardResponseDTO.Reply> toDTOList(List<Reply> replyList) {
        List<BoardResponseDTO.Reply> replyDTOList = new ArrayList<>();
        for (Reply reply : replyList) {
            BoardResponseDTO.Reply replyDTO = toDTO(reply);
            replyDTOList.add(replyDTO);
        }
        return replyDTOList;
    }


    ResponseDTO<Long> create(BoardRequestDTO.Reply replyDTO);

    //댓글 목록 조회
    ResponseDTO<List<BoardResponseDTO.Reply>> replyList(Long boardId);
}
