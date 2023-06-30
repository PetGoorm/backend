package com.petgoorm.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.todo.ToDoDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.ToDo;
import com.petgoorm.backend.service.ToDoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("todo")
@RequiredArgsConstructor
@Log4j2
public class ToDoController {

    private final ToDoService toDoService;

    //todolist 등록
    @PostMapping
    public ResponseDTO<ToDo> create(@RequestBody ToDoDTO dto, @RequestHeader("Authorization") String accessToken) {
            Member member = toDoService.getAuthenticatedMember(accessToken);
            ToDo entity = toDoService.toEntity(dto, member);
            return toDoService.create(entity);
    }

    //todolist 조회
    @GetMapping
    public ResponseDTO<List<ToDo>> retrieve(@RequestHeader("Authorization") String accessToken) {
        Member member = toDoService.getAuthenticatedMember(accessToken);
        return toDoService.retrieve(member);
    }

    //todolist 수정
    @PutMapping
    public ResponseDTO<ToDo> update(@RequestHeader("Authorization") String accessToken, @RequestBody ToDoDTO dto) {
        Member member = toDoService.getAuthenticatedMember(accessToken);
        ToDo entity = toDoService.toEntity(dto, member);
        return toDoService.update(entity);
    }

    //todolist 삭제
    @DeleteMapping("/{id}")
    public ResponseDTO<ToDo> delete(@RequestHeader("Authorization") String accessToken, @PathVariable("id") Long todoid) {
        return toDoService.delete(todoid);
    }
}
