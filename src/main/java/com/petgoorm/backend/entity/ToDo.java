package com.petgoorm.backend.entity;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.*;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="todo")
@ToString(exclude = "member")
public class ToDo extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(nullable = false, name = "done")
    private boolean done;

    @Column(name = "privacy")
    private String privacy;

    @Column(name="day")
    private LocalDate day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_member_id")
    private Member member;
}