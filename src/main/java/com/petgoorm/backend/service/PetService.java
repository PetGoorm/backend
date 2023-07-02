package com.petgoorm.backend.service;

import com.petgoorm.backend.dto.ResponseDTO;
import com.petgoorm.backend.dto.pet.PetDTO;
import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.Pet;



public interface PetService {

    //DTO->Entity 변환 메서드
    default Pet toEntity(PetDTO petDTO, Member member) {
        Pet pet = Pet.builder()
                .petname(petDTO.getPetname())
                .age(petDTO.getAge())
                .petUrl(petDTO.getPetUrl())
                .firstmet(petDTO.getFirstmet())
                .birth(petDTO.getBirth())
                .type(petDTO.getType())
                .weight(petDTO.getWeight())
                .member(member)
                .build();
        return pet;
    }

    default PetDTO toDTO(Pet pet){
        PetDTO petDTO = PetDTO.builder()
                .petId(pet.getPetId())
                .petUrl(pet.getPetUrl())
                .type(pet.getType())
                .weight(pet.getWeight())
                .petname(pet.getPetname())
                .age(pet.getAge())
                .firstmet(pet.getFirstmet())
                .build();
                return petDTO;
    }


    ResponseDTO<Long> petAdd(PetDTO petDTO);

    ResponseDTO<PetDTO> petInfo();

    ResponseDTO<PetDTO> updatePet(Long petId, PetDTO petDTO);

    ResponseDTO<Long> deletePet(Long petId);
}
