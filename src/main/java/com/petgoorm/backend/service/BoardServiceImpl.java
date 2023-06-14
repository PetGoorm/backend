package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.jwt.JwtTokenProvider;
import com.petgoorm.backend.repository.BoardRepository;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    final private BoardRepository boardRepository;

    final private MemberRepository memberRepository;

    final private JwtTokenProvider jwtTokenProvider;


    // 회원 인증 여부를 확인하는 서비스 내부 메서드
    private Member getAuthenticatedMember(String tokenWithoutBearer) {
        Authentication userDetails = jwtTokenProvider.getAuthentication(tokenWithoutBearer);
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인 상태를 확인 해 주세요.");
        }
        String email = userDetails.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
    }

    //board 조회 시 null인지 확인하는 내부 메서드
    private ResponseDTO<Optional<Board>> optionalBoard(Long boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "게시물 정보를 찾을 수 없습니다.", null);
        }
        return ResponseDTO.of(HttpStatus.OK.value(), null, optionalBoard);
    }


    //글 등록 => boardId 반환
    @Override
    @Transactional
    public ResponseDTO<Long> create(BoardRequestDTO boardDTO, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        Board board = createToEntity(boardDTO, member);
        try {
            boardRepository.save(board);
            return ResponseDTO.of(HttpStatus.OK.value(), "게시물 작성이 완료되었습니다.", board.getBoardId());
        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    //게시물 삭제
    @Override
    @Transactional
    public ResponseDTO<String> delete(Long boardId, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        Optional<Board> optionalBoard = optionalBoard(boardId).getData();

        Board board = optionalBoard.get();
        if (!board.getWriter().getId().equals(member.getId())) {
            return ResponseDTO.of(HttpStatus.FORBIDDEN.value(), "게시물 삭제 권한이 없습니다.", null);
        }
        try {
            boardRepository.deleteById(boardId);
            return ResponseDTO.of(HttpStatus.OK.value(), "게시물이 삭제되었습니다.", null);
        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }


    //글 조회
    @Override
    @Transactional
    public ResponseDTO<BoardResponseDTO> findOneBoard(Long boardId) {

        Optional<Board> optionalBoard = optionalBoard(boardId).getData();

        try {
            Board board = optionalBoard.get();
            BoardResponseDTO boardResponseDTO = toDTO(board);
            return ResponseDTO.of(HttpStatus.OK.value(), null, boardResponseDTO);
        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    @Override
    @Transactional
    public ResponseDTO<BoardResponseDTO.edit> updateGet(Long boardId, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        Optional<Board> optionalBoard = optionalBoard(boardId).getData();

        Board board = optionalBoard.get();
        boolean check = board.getWriter().getId().equals(member.getId());

        if (!check) {
            return ResponseDTO.of(HttpStatus.FORBIDDEN.value(), "게시물 수정 권한이 없습니다.", null);
        }

        BoardResponseDTO.edit editForm = createEditForm(board);

        return ResponseDTO.of(HttpStatus.OK.value(), null, editForm);
    }

    @Override
    @Transactional
    public ResponseDTO<Long> updatePut(Long boardId, BoardRequestDTO.edit editForm, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        Optional<Board> optionalBoard = optionalBoard(boardId).getData();

        Board board = optionalBoard.get();
        boolean check = board.getWriter().getId().equals(member.getId());

        if (!check) {
            return ResponseDTO.of(HttpStatus.FORBIDDEN.value(), "게시물 수정 권한이 없습니다.", null);
        }

        updateBoard(board, editForm);

        return ResponseDTO.of(HttpStatus.OK.value(), "게시물이 수정되었습니다.", board.getBoardId());
    }


}




