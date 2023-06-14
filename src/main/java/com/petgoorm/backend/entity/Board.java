package com.petgoorm.backend.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name="board")
@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@DynamicInsert
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @Column(name = "category")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(name = "writer_address",nullable = false)
    private String writerAddress;

    @Column(name = "writer_nickname",nullable = false)
    private String writerNickname;

    @Column(name = "click_cnt",nullable = false)
    @ColumnDefault("0")
    private Long clickCnt;

    @Column(name = "likes_cnt",nullable = false)
    @ColumnDefault("0")
    private Long likesCnt;

    @Column(name = "comment_cnt",nullable = false)
    @ColumnDefault("0")
    private Long commentCnt;

}
