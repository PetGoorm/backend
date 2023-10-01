package com.petgoorm.backend.repository;
import com.petgoorm.backend.entity.Board;
import com.petgoorm.backend.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByBoard(Board board);
    Optional<Reply> findById(Long rId);

}
