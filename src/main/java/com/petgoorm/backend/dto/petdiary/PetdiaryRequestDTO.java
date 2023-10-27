package com.petgoorm.backend.dto.petdiary;

import java.time.LocalDate;

import com.petgoorm.backend.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class PetdiaryRequestDTO {

    private String water;

    private String poop;

    private String snack;

    private String food;

    private String walk;

    private String diary;

    private LocalDate day;
}
