package com.petgoorm.backend.dto.todo;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TodoRepeatDTO {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private RepeatType repeatType;
    private Integer monthlyRepeatDay;
    private Map<String, Boolean> weeklyRepeatDays;
}
