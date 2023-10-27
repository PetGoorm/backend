package com.petgoorm.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.board.BoardResponseDTO;
import com.petgoorm.backend.dto.petdiary.PetdiaryRequestDTO;
import com.petgoorm.backend.dto.petdiary.PetdiaryResponseDTO;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.PetDiary;
import com.petgoorm.backend.entity.Reply;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.repository.PetDiaryRepository;
import com.petgoorm.backend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PetdiaryServiceImpl implements PetdiaryService{

    private final PetDiaryRepository petDiaryRepository;

    private final MemberRepository memberRepository;

    // 펫 다이어리 등록
    @Override
    public ResponseDTO<Long> diarycreate(PetdiaryRequestDTO petdiaryRequestDTO) {

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            PetDiary petDiary = toEntity(petdiaryRequestDTO, member);
            petDiaryRepository.save(petDiary);

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 다이어리 등록에 성공했습니다.", petDiary.getDiaryId());

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }
    }


    // 펫 다이어리 조회
    @Override
    public ResponseDTO<List<PetdiaryResponseDTO>> diaryread() {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            List<PetDiary> petDiaryList = petDiaryRepository.findByOwner(member);
            List<PetdiaryResponseDTO> petdiaryResponseDTOList = toDTOList(petDiaryList);

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 다이어리 조회에 성공했습니다.", petdiaryResponseDTOList);

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    // 펫 다이어리 하루 조회
    @Override
    public ResponseDTO<PetdiaryResponseDTO> oneread(LocalDate day) {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            PetDiary petDiaryList = petDiaryRepository.findByOwnerAndDay(member, day);
            System.out.println(petDiaryList);
            PetdiaryResponseDTO petdiaryResponseDTO = toDTO(petDiaryList);
            System.out.println(petdiaryResponseDTO);

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 다이어리 조회에 성공했습니다.", petdiaryResponseDTO);

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }
    }

    //펫 다이어리 수정
    @Override
    public ResponseDTO<PetDiary> diaryupdate(Long petdiaryid, PetdiaryRequestDTO dto){

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            PetDiary petDiary = petDiaryRepository.findById(petdiaryid).orElseThrow(()-> new NullPointerException("다이어리가 존재하지 않습니다."));
            petDiary.updatediary(dto.getWater(), dto.getPoop(), dto.getSnack(), dto.getFood(), dto.getWalk(), dto.getDiary(), dto.getDay());
            petDiaryRepository.save(petDiary);

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 다이어리 수정에 성공했습니다.", petDiary);

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }

    //펫 다이어리 삭제
    @Override
    public ResponseDTO<Long> diarydelete(Long petdiaryid){

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            Optional<PetDiary> petDiary = petDiaryRepository.findById(petdiaryid);
            petDiaryRepository.delete(petDiary.get());

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 다이어리 삭제에 성공했습니다.", petDiary.get().getDiaryId());

        } catch (Exception e) {
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);
        }

    }
}
