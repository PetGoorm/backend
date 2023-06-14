package com.petgoorm.backend.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class BoardResponseDTO {

    @NotBlank
    private Long boardId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String category;
    private String image;
    private String writerNickname;
    private String writerLocation;

    @Data
    @RequiredArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class edit{
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @NotBlank
        private String category;
        private String image;
    }


}
