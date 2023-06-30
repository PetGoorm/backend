package com.petgoorm.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petgoorm.backend.entity.Member;
import com.petgoorm.backend.entity.ToDo;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    List<ToDo> findByMember(Member member);
}
