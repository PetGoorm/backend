package com.petgoorm.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.petdiary.PetdiaryRequestDTO;
import com.petgoorm.backend.dto.petdiary.PetdiaryResponseDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.PetDiary;

public interface PetdiaryService {

    //펫 다이어리 등록
    ResponseDTO<Long> diarycreate(PetdiaryRequestDTO petdiaryRequestDTO);

    //해당 사용자의 모든 펫 다이어리 조회
    ResponseDTO<List<PetdiaryResponseDTO>> diaryread();

    //펫 다이어리 날짜 하루 조회
    ResponseDTO<PetdiaryResponseDTO> oneread(LocalDate day);

    //펫 다이어리 수정
    ResponseDTO<PetDiary> diaryupdate(Long petdiaryid, PetdiaryRequestDTO petdiaryRequestDTO);

    //펫 다이어리 삭제
    ResponseDTO<Long> diarydelete(Long petdiaryid);

    //DTO -> Entity 변환 메서드
    default PetDiary toEntity(PetdiaryRequestDTO petdiaryRequestDTO, Member member){
        PetDiary petDiary = PetDiary.builder()
            .water(petdiaryRequestDTO.getWater())
            .food(petdiaryRequestDTO.getFood())
            .poop(petdiaryRequestDTO.getPoop())
            .walk(petdiaryRequestDTO.getWalk())
            .snack(petdiaryRequestDTO.getSnack())
            .diary(petdiaryRequestDTO.getDiary())
            .day(petdiaryRequestDTO.getDay())
            .owner(member)
            .build();
        return petDiary;
    }

    //Entity -> DTO 변환 메서드
    default PetdiaryResponseDTO toDTO(PetDiary petDiary){
        PetdiaryResponseDTO petdiaryResponseDTO = PetdiaryResponseDTO.builder()
            .diaryId(petDiary.getDiaryId())
            .water(petDiary.getWater())
            .food(petDiary.getFood())
            .poop(petDiary.getPoop())
            .walk(petDiary.getWalk())
            .snack(petDiary.getSnack())
            .diary(petDiary.getDiary())
            .day(petDiary.getDay())
            .regdate(petDiary.getRegDate())
            .build();

        return petdiaryResponseDTO;
    }

    default List<PetdiaryResponseDTO> toDTOList(List<PetDiary> petDiaryList) {
        List<PetdiaryResponseDTO> petdiaryDTOList = new ArrayList<>();
        for (PetDiary diary : petDiaryList) {
            PetdiaryResponseDTO petdiaryDTO = toDTO(diary);
            petdiaryDTOList.add(petdiaryDTO);
        }
        return petdiaryDTOList;
    }
}
