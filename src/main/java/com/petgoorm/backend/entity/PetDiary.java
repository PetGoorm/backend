package com.petgoorm.backend.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name="petdiary")
@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class PetDiary extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diaryId")
    private Long diaryId;

    @Column(name="water")
    private String water;

    @Column(name="poop")
    private String poop;

    @Column(name="snack")
    private String snack;

    @Column(name="food")
    private String food;

    @Column(name="walk")
    private String walk;

    @Column(name="diary")
    private String diary;

    @Column(name = "day")
    private LocalDate day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    //내용 수정 메소드
    public void updatediary(String water, String poop, String snack, String food, String walk,
        String diary, LocalDate day){

        this.water = water;
        this.poop = poop;
        this.snack = snack;
        this.food = food;
        this.walk = walk;
        this.diary = diary;
        this.day = day;
    }
}
