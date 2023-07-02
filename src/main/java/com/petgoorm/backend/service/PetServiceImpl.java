package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.pet.PetDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.Pet;
import com.petgoorm.backend.repository.MemberRepository;
import com.petgoorm.backend.repository.PetRepository;
import com.petgoorm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public ResponseDTO<Long> petAdd(PetDTO petDTO){

        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        try {
            Pet pet = toEntity(petDTO, member);
            petRepository.save(pet);

            return ResponseDTO.of(HttpStatus.OK.value(), "펫 등록에 성공했습니다.", pet.getPetId());

        }
        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }

    }

    @Transactional(readOnly = true)
    @Override
    public ResponseDTO<PetDTO> petInfo(){
        // 현재 로그인한 사용자의 Member 정보 가져오기
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        Pet pet = petRepository.findByMember(member).orElseThrow(()-> new NullPointerException("펫이 존재하지 않습니다."));
        try{
            PetDTO petDTO = toDTO(pet);
            return ResponseDTO.of(HttpStatus.OK.value(), "펫 정보 조회에 성공했습니다.", petDTO);

        }catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }

    }

    @Transactional
    @Override
    public ResponseDTO<PetDTO> updatePet(Long petId, PetDTO petDTO){

        try {

            Pet pet = petRepository.findById(petId).orElseThrow(() -> new NullPointerException("펫이 존재하지 않습니다."));

            //더티체킹(save 불필요)
            pet.UpdatePet(petDTO);

            log.info("pet " + pet);
            return ResponseDTO.of(HttpStatus.OK.value(), "펫 수정에 성공했습니다.", null);

        }
        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }


    }

    //펫 삭제 서비스
    @Transactional
    @Override
    public ResponseDTO<Long> deletePet(Long petId){

        try {
            petRepository.deletePetByPetId(petId);
            return ResponseDTO.of(HttpStatus.OK.value(), "펫 삭제에 성공했습니다.", petId);
        }
        catch (Exception e){
            return ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예기치 못한 에러가 발생했습니다.", null);

        }


    }





}
