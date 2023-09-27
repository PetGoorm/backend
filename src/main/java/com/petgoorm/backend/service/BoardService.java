package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;


public interface BoardService {


    public ResponseDTO<Long> create(BoardRequestDTO boardRequestDTO,String accessToken);
    public ResponseDTO<String> delete(Long boardId, String accessToken);

    @Transactional
    ResponseDTO<BoardResponseDTO> getOneBoard(Long boardId, String tokenWithoutBearer);

    ResponseDTO<Page<BoardResponseDTO>> getBoardPage(Pageable pageable, String category, String keyword, String search);

    public ResponseDTO<Long> updatePut(Long boardId, BoardRequestDTO.edit boardDTO, String accessToken);

    public default Board createToEntity(BoardRequestDTO dto, Member member) {

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .image(dto.getImage())
                .category(dto.getCategory())
                .writer(member)
                .build();
        return board;
    }

    public default BoardResponseDTO toDTO(Board board) {
        BoardResponseDTO boardDTO = BoardResponseDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .category(board.getCategory())
                .writerLocation(board.getWriter().getAddress().replaceAll("\\\"","").trim())
                .writerNickname(board.getWriter().getNickname())
                .image(board.getImage())
                .regdate(board.getRegDate())
                .moddate(board.getModDate())
                // 필요한 속성들을 설정
                .build();
        return boardDTO;
    }

    //수정 - put board form
    public default Board updateBoard(Board board, BoardRequestDTO.edit editForm) {
        board.setTitle(editForm.getTitle());
        board.setContent(editForm.getContent());
        board.setCategory(editForm.getCategory());
        board.setImage(editForm.getImage());
        return board;
    }


    //게시글 목록 조회 (카테고리 기반, 페이징)
    public default List<BoardResponseDTO> toDTOList(List<Board> boardList) {
        List<BoardResponseDTO> boardDTOList = new ArrayList<>();
        for (Board board : boardList) {
            BoardResponseDTO boardDTO = toDTO(board);
            boardDTOList.add(boardDTO);
        }
        return boardDTOList;

    }

    //지역별 게시판 글 목록 조회
    ResponseDTO<List<BoardResponseDTO>> getRegionBoardList(String tokenWithoutBearer);

    //세번째 공백전까지 주소 슬라이스
    String sliceAddress(String input);
}