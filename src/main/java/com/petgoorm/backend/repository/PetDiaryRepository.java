package com.petgoorm.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.PetDiary;

public interface PetDiaryRepository extends JpaRepository<PetDiary, Long> {

    List<PetDiary> findByOwner(Member member);


    Optional<PetDiary> findByOwnerAndDay(Member member, LocalDate day);

}
