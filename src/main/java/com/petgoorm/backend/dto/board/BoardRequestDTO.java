package com.petgoorm.backend.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class BoardRequestDTO {

    @NotBlank
    private Long boardId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String category;
    private String image;

    @Data
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class edit {
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @NotBlank
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

