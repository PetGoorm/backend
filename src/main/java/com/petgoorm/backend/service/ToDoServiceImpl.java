package com.petgoorm.backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.todo.ToDoDTO;
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
    public ResponseDTO<ToDo> create(ToDoDTO toDoDTO, String accessToken) {

        Member member = getAuthenticatedMember(accessToken);
        ToDo entity = toEntity(toDoDTO, member);
        //데이터 삽입
        ToDo savedToDo = toDoRepository.save(entity);
        //userId에 해당하는 전제 데이터 리턴
        return ResponseDTO.of(HttpStatus.OK.value(), "todo 작성이 완료되었습니다.", savedToDo);
    }

    @Override
    public ResponseDTO<List<ToDo>> retrieve(String accessToken) {

        Member member = getAuthenticatedMember(accessToken);

        //userId에 해당하는 전제 데이터 리턴
        List<ToDo> toDos = toDoRepository.findByMember(member);

        return ResponseDTO.of(HttpStatus.OK.value(), "todo 조회가 완료되었습니다.", toDos);
    }

    @Override
    public ResponseDTO<List<ToDo>> getDayTodo(String accessToken, LocalDate day) {

        Member member = getAuthenticatedMember(accessToken);

        // 선택한 날짜에 해당하는 데이터 조회
        List<ToDo> toDos = toDoRepository.findByMemberAndDay(member, day);

        return ResponseDTO.of(HttpStatus.OK.value(), "todo 날짜별 조회가 완료되었습니다..", toDos);
    }


    @Override
    public ResponseDTO<ToDo> update(ToDoDTO toDoDTO, Long todoId, String accessToken) {

        Member member = getAuthenticatedMember(accessToken);

        // 데이터의 존재 여부를 확인
        Optional<ToDo> original = toDoRepository.findById(todoId);

        // 데이터가 존재하는 경우에만 작업 수행
        if (original.isPresent()) {
            ToDo todo = original.get();
            todo.setTitle(toDoDTO.getTitle());
            todo.setDone(toDoDTO.isDone());
            toDoRepository.save(todo);

            // userId에 해당하는 전체 데이터 리턴
            return ResponseDTO.of(HttpStatus.OK.value(), "todo 수정이 완료되었습니다.", todo);
        } else {

            // 데이터가 없는 경우 메시지 출력
            return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "데이터가 없습니다.", null);
        }
    }

    @Override
    public ResponseDTO<ToDo> delete(Long entity, String accessToken) {

        Member member = getAuthenticatedMember(accessToken);

        try {
            // 데이터의 존재 여부를 확인
            final Optional<ToDo> original = toDoRepository.findById(entity);

            // 데이터가 존재하는 경우에만 삭제 수행
            if (original.isPresent()) {

                toDoRepository.delete(original.get());
                // userId에 해당하는 전체 데이터 리턴
                return ResponseDTO.of(HttpStatus.OK.value(), "todo 삭제가 완료되었습니다.", original.get());
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