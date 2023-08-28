package com.petgoorm.backend.repository;

import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    public List<Board> findByOrderByRegDateDesc();
    List<Board> findByWriterAddressContainingOrderByRegDateDesc(String address);


}
