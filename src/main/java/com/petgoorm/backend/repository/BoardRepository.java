package com.petgoorm.backend.repository;

import com.petgoorm.backend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    public Page<Board> findByBcodeOrderByBoardIdDesc(String bcode, Pageable pageable);
    public Page<Board> findByOrderByBoardIdDesc(Pageable pageable);
    Page<Board> findByCategoryOrderByBoardIdDesc(String category, Pageable pageable);
    List<Board> findByWriterAddressContainingOrderByRegDateDesc(String address);
    Page<Board> findByBcodeAndCategoryOrderByBoardIdDesc(String bcode, String category, Pageable pageable);
    Page<Board> findByTitleContainingOrderByBoardIdDesc(String keyword, Pageable pageable);
    Page<Board> findByTitleContainingOrContentContainingOrderByBoardIdDesc(String title, String content, Pageable pageable);
    Page<Board> findAllByOrderByRegDateDesc (PageRequest pageRequest);
    Page<Board> findByCategoryAndTitleContainingOrderByBoardIdDesc(String category, String keyword, Pageable pageable);
    Page<Board> findByCategoryAndTitleContainingOrContentContainingOrderByBoardIdDesc(String category, String title, String content, Pageable pageable);
    Page<Board> findByBcodeAndCategoryAndTitleContainingOrderByBoardIdDesc(String bcode, String category, String keyword, Pageable pageable);
    Page<Board> findByBcodeAndCategoryAndTitleContainingOrContentContainingOrderByBoardIdDesc (String bcode, String category, String title, String content, Pageable pageable);

    // 캐쉬 조회수를 DB 조회수에 반영하는 쿼리 메서드
    @Query(value = "UPDATE board SET click_cnt = :viewCnt WHERE board_id = :boardId", nativeQuery = true)
    void updateClickCntByBoardId(@Param("boardId")Long boardId, @Param("viewCnt")Long viewCnt);


    // DB에서 조회수만 가져오는 쿼리 메서드
    @Query("SELECT b.clickCnt FROM Board b WHERE b.boardId = :boardId")
    Long findClickCntByBoardId(@Param("boardId") Long boardId);

}
