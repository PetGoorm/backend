package com.petgoorm.backend.dto.board;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class BoardRequestDTO {

    private String title;
    private String content;
    private String category;
    private String image;
    private String writerNickname;
    private String writerAddress;

    @Data
    @Builder
    @AllArgsConstructor
    public static class edit {
        private String title;
        private String content;
        private String category;
        private String image;

    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reply {

        private String content;
        private Long boardId;

    }
}

