package com.petgoorm.backend.service;


import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardRequestDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.Reply;
import com.petgoorm.backend.repository.BoardRepository;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.repository.ReplyRepository;
import com.petgoorm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    //댓글 등록
    @Override
    public ResponseDTO<Long> create(BoardRequestDTO.Reply replyDTO) {

        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        Optional<Board> board = boardRepository.findById(replyDTO.getBoardId());

        try {
            Reply reply = toEntity(replyDTO, member, board.get());
            replyRepository.save(reply);

            return ResponseDTO.of(HttpStatus.OK.value(), "댓글 등록에 성공했습니다.", reply.getRId());

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }


    }

    //댓글 목록 조회
    @Override
    public ResponseDTO<List<BoardResponseDTO.Reply>> replyList(Long boardId) {

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        Optional<Board> opBoard = boardRepository.findById(boardId);

        List<BoardResponseDTO.Reply> replyDTOList = new ArrayList<>();

        try {
            if (opBoard.isPresent()) {
                Board board = opBoard.get();
                List<Reply> replyList = replyRepository.findByBoard(board);
                replyDTOList = toDTOList(replyList);
            }

            return ResponseDTO.of(HttpStatus.OK.value(), "댓글 조회에 성공했습니다.", replyDTOList);

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }


}
