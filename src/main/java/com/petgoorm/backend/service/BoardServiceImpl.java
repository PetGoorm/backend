package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.jwt.JwtTokenProvider;
import com.petgoorm.backend.repository.BoardRepository;
import com.petgoorm.backend.repository.MemberRepository;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    final private BoardRepository boardRepository;

    final private MemberRepository memberRepository;

    final private JwtTokenProvider jwtTokenProvider;

    final private RedisBoardService redisBoardService;

    // 회원 인증 여부를 확인하는 서비스 내부 메서드
    private Member getAuthenticatedMember(String tokenWithoutBearer) {
        try {
            Authentication userDetails = jwtTokenProvider.getAuthentication(tokenWithoutBearer);
            if (userDetails == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인 상태를 확인 해 주세요.");
            }
            String email = userDetails.getName();

            return memberRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 인증 정보입니다.", null);
        }
    }

    //board 조회 시 null인지 확인하는 내부 메서드
    private ResponseDTO<Optional<Board>> optionalBoard(Long boardId) {
        try {
            Optional<Board> optionalBoard = boardRepository.findById(boardId);
            return ResponseDTO.of(HttpStatus.OK.value(), null, optionalBoard);
        } catch (Exception e) {
            log.error("DB 조회 중 예외 발생: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND.value(), "서버에 오류가 발생했습니다.", null);
        }
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
            log.error("게시물 작성 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
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
            log.error("게시물 삭제 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
        }
    }

    //글 조회
    @Override
    @Transactional
    public ResponseDTO<BoardResponseDTO> getOneBoard(Long boardId, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        if (member == null) {
            // 인증되지 않은 사용자에게 에러 메시지 반환
            return ResponseDTO.of(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용해주세요.", null);
        }
        Optional<Board> optionalBoard = optionalBoard(boardId).getData();
        try {
            Board board = optionalBoard.get();

            //조회수 증가 메서드
            boolean duplViewCheck = redisBoardService.isDuplicateView(member.getId(), boardId);
            //조회수 중복 확인 메서드
            if (duplViewCheck == false) {
                redisBoardService.RedisGetOrIncementBoardViewCount(boardId);
            }

            BoardResponseDTO boardResponseDTO = toDTO(board);

            return ResponseDTO.of(HttpStatus.OK.value(), null, boardResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("게시물 조회 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
        }
    }

    // 게시물 조회
    @Override
    @Transactional
    public ResponseDTO<Page<BoardResponseDTO>> getBoardPage(String tokenWithoutBearer, Pageable pageable, String category, String search, String keyword) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        if (member == null) {
            return ResponseDTO.of(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용해주세요.", null);
        }

        try {
            Page<Board> boards;
            if ("all".equals(category)) {
                if (StringUtils.isEmpty(keyword)) { // 키워드 없을 때
                    boards = boardRepository.findByBcodeOrderByBoardIdDesc(member.getBcode(), pageable);
                } else { // 키워드 있을 때
                    if ("title".equals(search)) { // 제목 검색
                        boards = boardRepository.findByTitleContainingOrderByBoardIdDesc(keyword, pageable);
                    } else { // 제목+내용 검색
                        boards = boardRepository.findByTitleContainingOrContentContainingOrderByBoardIdDesc(keyword, keyword, pageable);
                    }
                }
            } else { // 특정 카테고리
                if (StringUtils.isEmpty(keyword)) { // 키워드 없을 때
                    boards = boardRepository.findByBcodeAndCategoryOrderByBoardIdDesc(member.getBcode(), category, pageable);
                } else { // 키워드 있을 때
                    if ("title".equals(search)) { // 제목 검색
                        boards = boardRepository.findByBcodeAndCategoryAndTitleContainingOrderByBoardIdDesc(member.getBcode(), category, keyword, pageable);
                    } else { // 제목+내용 검색
                        boards = boardRepository.findByBcodeAndCategoryAndTitleContainingOrContentContainingOrderByBoardIdDesc(member.getBcode(), category, keyword, keyword, pageable);
                    }
                }
            }

            if (boards.isEmpty()) {
                return ResponseDTO.of(HttpStatus.NO_CONTENT.value(), "게시물이 없습니다.", null);
            } else {
                Page<BoardResponseDTO> boardDTOPage = boards.map(board -> toDTO(board));
                return ResponseDTO.of(HttpStatus.OK.value(), null, boardDTOPage);
            }
        } catch (Exception e) {
            log.error("게시판 글 목록 조회 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
        }
    }

    // 게시물 수정
    @Override
    @Transactional
    public ResponseDTO<Long> updatePut(Long boardId, BoardRequestDTO.edit editForm, String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        Optional<Board> optionalBoard = optionalBoard(boardId).getData();

        Board board = optionalBoard.get();
        boolean check = board.getWriter().getId().equals(member.getId());

        try {
            if (!check) {
                return ResponseDTO.of(HttpStatus.FORBIDDEN.value(), "게시물 수정 권한이 없습니다.", null);
            } else {
                updateBoard(board, editForm);
                return ResponseDTO.of(HttpStatus.OK.value(), "게시물이 수정되었습니다.", board.getBoardId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("게시물 수정 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
        }

    }

    //지역별 게시판 글 목록 조회
    @Override
    @Transactional
    public ResponseDTO<List<BoardResponseDTO>> getRegionBoardList(String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        log.info("로그인한 멤버의 주소" + member.getAddress());
        String sliceAddress = sliceAddress(member.getAddress());
        try {
            List<Board> boardList = boardRepository.findByWriterAddressContainingOrderByRegDateDesc(sliceAddress);
            List<BoardResponseDTO> boardDTOList = toDTOList(boardList);

            if (boardDTOList == null || boardDTOList.isEmpty()) {
                return ResponseDTO.of(HttpStatus.NO_CONTENT.value(), "게시물이 없습니다.", null);
            }

            return ResponseDTO.of(HttpStatus.OK.value(), null, boardDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    //세번째 공백전까지 주소 슬라이스
    @Override
    public String sliceAddress(String input) {
        String[] arrays = input.split(" ");
        //서울 서초구 잠원동까지 잘리는 주소
        String sliced = String.join(" ", Arrays.copyOfRange(arrays, 0, 3));
        log.info("자른 주소: " + sliced);
        return sliced;
    }

    //최신 5개 게시물 조회
    @Override
    public ResponseDTO<Page<BoardResponseDTO>> getRecentlyBoards(String tokenWithoutBearer) {
        Member member = getAuthenticatedMember(tokenWithoutBearer);
        if (member == null) {
            return ResponseDTO.of(HttpStatus.UNAUTHORIZED.value(), "로그인 후 이용해주세요.", null);
        }

        // 페이지 번호를 0, 페이지 사이즈를 5로 설정하여 최신 5개의 게시물 조회
        try {
            PageRequest recentlyboards = PageRequest.of(0, 5);
            // 생성일자 기준으로 내림차순 정렬하여 조회
            Page<Board> boards = boardRepository.findAllByOrderByRegDateDesc(recentlyboards);
            if (boards.isEmpty()) {
                return ResponseDTO.of(HttpStatus.NO_CONTENT.value(), "최신 게시물이 없습니다.", null);
            } else {
                Page<BoardResponseDTO> boardDTOPage = boards.map(board -> toDTO(board));
                return ResponseDTO.of(HttpStatus.OK.value(), null, boardDTOPage);
            }
        } catch (Exception e) {
            log.error("게시판 글 목록 조회 중 예외 발생: {}", e.getMessage(), e);
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 발생했습니다.", null);
        }


    }
}



