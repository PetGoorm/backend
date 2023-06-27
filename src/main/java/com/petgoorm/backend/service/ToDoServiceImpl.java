package com.petgoorm.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.ToDo;
import com.petgoorm.backend.jwt.JwtTokenProvider;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.repository.ToDoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToDoServiceImpl implements ToDoService{

    private final ToDoRepository toDoRepository;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public Member getAuthenticatedMember(String accessToken) {

        String tokenWithoutBearer = accessToken.replace("Bearer ", "");
        Authentication userDetails = jwtTokenProvider.getAuthentication(tokenWithoutBearer);
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인 상태를 확인 해 주세요.");
        }
        String email = userDetails.getName();

        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
    }

    @Override
    public ResponseDTO<ToDo> create(ToDo entity) {

        //데이터 삽입
        toDoRepository.save(entity);

        //userId에 해당하는 전제 데이터 리턴
        return ResponseDTO.of(HttpStatus.OK.value(), "todo 작성이 완료되었습니다.", entity);
    }

    @Override
    public ResponseDTO<List<ToDo>> retrieve(Member member) {

        //userId에 해당하는 전제 데이터 리턴
        List<ToDo> toDos = toDoRepository.findByMember(member);

        return ResponseDTO.of(HttpStatus.OK.value(), "todo 조회가 완료되었습니다.", toDos);
    }

    @Override
    public ResponseDTO<ToDo> update(ToDo entity) {

        // 데이터의 존재 여부를 확인
        final Optional<ToDo> original = toDoRepository.findById(entity.getId());

        // 데이터가 존재하는 경우에만 작업 수행
        if (original.isPresent()) {
            ToDo todo = original.get();
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            todo.setDay(entity.getDay());
            toDoRepository.save(todo);

            // userId에 해당하는 전체 데이터 리턴
            return ResponseDTO.of(HttpStatus.OK.value(), "todo 수정이 완료되었습니다.", entity);
        } else {

            // 데이터가 없는 경우 메시지 출력
            return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "데이터가 없습니다.", null);
        }
    }

    @Override
    public ResponseDTO<ToDo> delete(ToDo entity) {

        try {
            // 데이터의 존재 여부를 확인
            final Optional<ToDo> original = toDoRepository.findById(entity.getId());

            // 데이터가 존재하는 경우에만 삭제 수행
            if (original.isPresent()) {

                toDoRepository.delete(entity);
                // userId에 해당하는 전체 데이터 리턴
                return ResponseDTO.of(HttpStatus.OK.value(), "todo 삭제가 완료되었습니다.", entity);
            } else {

                // 데이터가 없는 경우 메시지 출력
                return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "데이터가 없습니다.", null);
            }

        } catch (Exception e) {

            log.error("삭제 실패");
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "삭제 실패", null);
        }
    }

}