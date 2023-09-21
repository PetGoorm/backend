package com.petgoorm.backend.repository;

import com.petgoorm.backend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

//    public List<Board> findByOrderByRegDateDesc();
    public Page<Board> findByOrderByBoardIdDesc(Pageable pageable);
    Page<Board> findByCategoryOrderByBoardIdDesc(String category, Pageable pageable);
    List<Board> findByWriterAddressContainingOrderByRegDateDesc(String address);

    Page<Board> findByTitleContainingOrderByBoardIdDesc(String keyword, Pageable pageable);
    Page<Board> findByTitleContainingOrContentContainingOrderByBoardIdDesc(String title, String content, Pageable pageable);

    Page<Board> findByCategoryAndTitleContainingOrderByBoardIdDesc(String category, String keyword, Pageable pageable);
    Page<Board> findByCategoryAndTitleContainingOrContentContainingOrderByBoardIdDesc(String category, String title, String content, Pageable pageable);


}
