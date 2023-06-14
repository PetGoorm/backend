package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;


public interface BoardService {


    public ResponseDTO<Long> create(BoardRequestDTO boardRequestDTO,String accessToken);
    public ResponseDTO<String> delete(Long boardId, String accessToken);

    public ResponseDTO<BoardResponseDTO> findOneBoard(Long boardId);
    public ResponseDTO<BoardResponseDTO.edit> updateGet(Long boardId, String accessToken);
    public ResponseDTO<Long> updatePut(Long boardId, BoardRequestDTO.edit boardDTO, String accessToken);

    public default Board createToEntity(BoardRequestDTO dto, Member member) {

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .image(dto.getImage())
                .category(dto.getCategory())
                .writer(member)
                .writerAddress(member.getAddress())
                .writerNickname(member.getNickname())
                .build();
        return board;
    }

    public default BoardResponseDTO toDTO(Board board) {
        BoardResponseDTO dto = new BoardResponseDTO();
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setImage(board.getImage());
        dto.setWriterNickname(board.getWriterNickname());
        //주소에 특정 부분만 잘라서 dto에 담음 - 추후 더 정교하게 지역 나눌 예정
        dto.setWriterLocation(board.getWriterAddress().replaceAll("\\\"","").trim());
        return dto;
    }


    //수정 - get board form
    public default BoardResponseDTO.edit createEditForm(Board board) {
        return BoardResponseDTO.edit.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .image(board.getImage())
                .category(board.getCategory())
                .build();
    }


    //수정 - put board form
    public default Board updateBoard(Board board, BoardRequestDTO.edit editForm) {
        board.setTitle(editForm.getTitle());
        board.setContent(editForm.getContent());
        board.setCategory(editForm.getCategory());
        board.setImage(editForm.getImage());
        return board;
    }

}