package com.petgoorm.backend.service;

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

    ResponseDTO<ToDo> create(final ToDo entity);
    ResponseDTO<List<ToDo>> retrieve(final Member member);
    ResponseDTO<ToDo> update(final ToDo entity);
    ResponseDTO<ToDo> delete(final Long entity);

    Member getAuthenticatedMember(String accessToken);
}