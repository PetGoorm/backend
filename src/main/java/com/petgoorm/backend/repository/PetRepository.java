package com.petgoorm.backend.repository;

import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByMember(Member member);
    Optional<Pet> findById(Long petId);
    Optional<Pet> deletePetByPetId(Long petId);
    Optional<Pet> deletePetByMemberId(Long memberId);

}
