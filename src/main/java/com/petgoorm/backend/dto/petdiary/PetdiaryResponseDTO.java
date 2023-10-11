package com.petgoorm.backend.dto.petdiary;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class PetdiaryResponseDTO {

    private Long diaryId;

    private String water;

    private String poop;

    private String snack;

    private String food;

    private String walk;

    private String diary;

    private LocalDate day;

    @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
    private LocalDateTime regdate;

    @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
    private LocalDateTime moddate;
}
