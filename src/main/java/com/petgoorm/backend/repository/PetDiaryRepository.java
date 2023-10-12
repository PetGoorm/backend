package com.petgoorm.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.PetDiary;

public interface PetDiaryRepository extends JpaRepository<PetDiary, Long> {

    List<PetDiary> findByOwner(Member member);

    PetDiary findByOwnerAndDay(Member member, LocalDate day);
}
