package com.petgoorm.backend.dto.todo;

import com.petgoorm.backend.entity.ToDo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ToDoDTO {

    private Long id;
    private String title;
    private boolean done;
    private Date day;

}