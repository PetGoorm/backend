package com.petgoorm.backend.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.sun.xml.bind.v2.TODO;

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

            return toDoService.create(dto, accessToken);
    }

    //todolist 조회
    @GetMapping
    public ResponseDTO<List<ToDo>> retrieve(@RequestHeader("Authorization") String accessToken) {

        return toDoService.retrieve(accessToken);
    }

    //todolist 날짜별 조회
    @GetMapping("/{day}")
    public ResponseDTO<List<ToDo>> getDayTodo(@RequestHeader("Authorization") String accessToken,
        @PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate day) {

        return toDoService.getDayTodo(accessToken, day);
    }

    //todolist 수정
    @PutMapping("/{id}")
    public ResponseDTO<ToDo> update(@RequestHeader("Authorization") String accessToken, @PathVariable("id") Long todoid, @RequestBody ToDoDTO dto) {

        return toDoService.update(dto, todoid, accessToken);
    }

    //todolist 삭제
    @DeleteMapping("/{id}")
    public ResponseDTO<ToDo> delete(@RequestHeader("Authorization") String accessToken, @PathVariable("id") Long todoid) {

        return toDoService.delete(todoid, accessToken);
    }
}
