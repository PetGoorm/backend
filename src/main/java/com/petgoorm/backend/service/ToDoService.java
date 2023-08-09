package com.petgoorm.backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.todo.ToDoDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.ToDo;

public interface ToDoService {

    //DTO->Entity 변환 메서드
    public default ToDo toEntity(ToDoDTO toDoDTO, Member member){
        ToDo toDo = ToDo.builder()
            .id(toDoDTO.getId())
            .title(toDoDTO.getTitle())
            .done(toDoDTO.isDone())
            .day(toDoDTO.getDay())
            .member(member)
            .build();

        return toDo;
    }

    public default ToDoDTO toDTO(ToDo toDo){
        ToDoDTO toDoDTO = ToDoDTO.builder()
            .id(toDo.getId())
            .title(toDo.getTitle())
            .done(toDo.isDone())
            .day(toDo.getDay())
            .build();

        return toDoDTO;
    }

    ResponseDTO<ToDo> create(final ToDoDTO toDoDTO, String accessToken);
    ResponseDTO<List<ToDo>> retrieve(final String accessToken);
    ResponseDTO<List<ToDo>> getDayTodo(final String accessToken, LocalDate day);
    ResponseDTO<ToDo> update(final ToDoDTO toDoDTO, Long todoId, String accessToken);
    ResponseDTO<ToDo> delete(final Long entity, String accessToken);

    Member getAuthenticatedMember(String accessToken);
}