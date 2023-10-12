package com.petgoorm.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.petgoorm.backend.dto.petdiary.PetdiaryRequestDTO;
import com.petgoorm.backend.dto.petdiary.PetdiaryResponseDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.PetDiary;
import com.petgoorm.backend.service.PetdiaryService;
import com.petgoorm.backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("petdiary")
@RequiredArgsConstructor
@Log4j2
public class PetdiaryController {

    private final PetdiaryService petdiaryService;

    //펫 다이어리 등록
    @PostMapping
    public ResponseDTO<Long> diarycreate(@RequestBody PetdiaryRequestDTO petdiaryDTO) {
        return petdiaryService.diarycreate(petdiaryDTO);
    }

    //펫 다이어리 조회
    @GetMapping
    public ResponseDTO<List<PetdiaryResponseDTO>> diaryread(){
        return petdiaryService.diaryread();
    }


    @GetMapping("/{day}")
    public ResponseDTO<PetdiaryResponseDTO> oneread(@PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate day) {
        return petdiaryService.oneread(day);
    }

    //펫 다이어리 수정
    @PutMapping("/{petdiaryid}")
    public ResponseDTO<PetDiary> diaryupdate(@PathVariable("petdiaryid") Long petdiaryid, @RequestBody PetdiaryRequestDTO dto) {
        return petdiaryService.diaryupdate(petdiaryid, dto);
    }

    //펫 다이어리 삭제
    @DeleteMapping("/{petdiaryid}")
    public ResponseDTO<Long> diarydelete(@PathVariable("petdiaryid") Long petdiaryid) {
        return petdiaryService.diarydelete(petdiaryid);
    }
}
