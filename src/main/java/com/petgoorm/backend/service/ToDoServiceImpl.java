package com.petgoorm.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.todo.RepeatType;
import com.petgoorm.backend.dto.todo.ToDoDTO;
import com.petgoorm.backend.dto.todo.TodoRepeatDTO;
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
public class ToDoServiceImpl implements ToDoService {

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
        ToDo savedToDo = toDoRepository.save(entity);
        return ResponseDTO.of(HttpStatus.OK.value(), "todo 작성이 완료되었습니다.", savedToDo);
    }

    @Override
    public ResponseDTO<List<ToDo>> retrieve(String accessToken) {
        Member member = getAuthenticatedMember(accessToken);
        List<ToDo> toDos = toDoRepository.findByMember(member);
        return ResponseDTO.of(HttpStatus.OK.value(), "todo 조회가 완료되었습니다.", toDos);
    }

    @Override
    public ResponseDTO<List<ToDo>> getDayTodo(String accessToken, LocalDate day) {
        Member member = getAuthenticatedMember(accessToken);
        List<ToDo> toDos = toDoRepository.findByMemberAndDay(member, day);
        return ResponseDTO.of(HttpStatus.OK.value(), "todo 날짜별 조회가 완료되었습니다..", toDos);
    }

    @Override
    public ResponseDTO<ToDo> update(ToDoDTO toDoDTO, Long todoId, String accessToken) {
        Member member = getAuthenticatedMember(accessToken);
        Optional<ToDo> original = toDoRepository.findById(todoId);
        if (original.isPresent()) {
            ToDo todo = original.get();
            todo.setTitle(toDoDTO.getTitle());
            todo.setDone(toDoDTO.isDone());
            toDoRepository.save(todo);
            return ResponseDTO.of(HttpStatus.OK.value(), "todo 수정이 완료되었습니다.", todo);
        } else {
            return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "데이터가 없습니다.", null);
        }
    }

    @Override
    public ResponseDTO<ToDo> delete(Long entity, String accessToken) {
        Member member = getAuthenticatedMember(accessToken);
        try {
            final Optional<ToDo> original = toDoRepository.findById(entity);
            if (original.isPresent()) {
                toDoRepository.delete(original.get());
                return ResponseDTO.of(HttpStatus.OK.value(), "todo 삭제가 완료되었습니다.", original.get());
            } else {
                return ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "데이터가 없습니다.", null);
            }
        } catch (Exception e) {
            log.error("삭제 실패");
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "삭제 실패", null);
        }
    }

    @Override
    public ResponseDTO<List<ToDo>> createWithRepeat(TodoRepeatDTO repeatDTO, String accessToken, Long todoId) {
        Member member = getAuthenticatedMember(accessToken);
        List<ToDo> createdToDos = new ArrayList<>();

        LocalDate startDate = repeatDTO.getStartDate();
        LocalDate endDate = repeatDTO.getEndDate();
        RepeatType repeatType = repeatDTO.getRepeatType();
        Map<String, Boolean> weeklyRepeatDays = repeatDTO.getWeeklyRepeatDays();
        Integer monthlyRepeatDay = repeatDTO.getMonthlyRepeatDay();

        List<Integer> trueDays = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : weeklyRepeatDays.entrySet()) {
            if (entry.getValue()) { // 값이 true인 경우
                // 요일 문자열을 숫자로 변환하여 리스트에 추가합니다.
                switch (entry.getKey()) {
                    case "월":
                        trueDays.add(1);
                        break;
                    case "화":
                        trueDays.add(2);
                        break;
                    case "수":
                        trueDays.add(3);
                        break;
                    case "목":
                        trueDays.add(4);
                        break;
                    case "금":
                        trueDays.add(5);
                        break;
                    case "토":
                        trueDays.add(6);
                        break;
                    case "일":
                        trueDays.add(7);
                        break;
                    default:
                        // 이 외의 경우에는 처리하지 않음
                        break;
                }
            }
        }

        while (!startDate.isAfter(endDate)) {
            if (repeatType == RepeatType.WEEKLY) {
                startDate = startDate.plusDays(1);
                Integer dayOfWeek = startDate.getDayOfWeek().getValue();
  
                if (trueDays.contains(dayOfWeek)) {
                    createdToDos.add(createToDoAndSave(todoId, startDate));
                }
            } else if (repeatType == RepeatType.MONTHLY) {
                startDate = startDate.withDayOfMonth(monthlyRepeatDay);
                createdToDos.add(createToDoAndSave(todoId, startDate));
                startDate = startDate.plusMonths(1);
            } else if (repeatType == RepeatType.DAILY) {
                startDate = startDate.plusDays(1);
                createdToDos.add(createToDoAndSave(todoId, startDate));
            }
        }

        return ResponseDTO.of(HttpStatus.OK.value(), "반복 todo 작성이 완료되었습니다.", createdToDos);
    }

    private ToDo createToDoAndSave(Long todoId, LocalDate date) {
        Optional<ToDo> original = toDoRepository.findById(todoId);
        if (!original.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "todo가 없습니다.");
        }
        ToDo todo = new ToDo();
        ToDo originalTodo = original.get();
        todo.setTitle(originalTodo.getTitle());
        todo.setMember(originalTodo.getMember());
        todo.setDay(date);
        return toDoRepository.save(todo);
    }
}
