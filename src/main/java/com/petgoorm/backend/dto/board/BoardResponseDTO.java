package com.petgoorm.backend.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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
    @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
    private LocalDateTime regdate;
    @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
    private LocalDateTime moddate;
    private Long clickCnt;
    private String bcode;


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

    @Data
    @RequiredArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Reply{

        private Long rId;

        private String content;

        @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
        private LocalDateTime regdate;
        @JsonFormat(timezone = "YYYY-MM-DD HH:mm")
        private LocalDateTime moddate;

        private String writerNickname;

        //본인 댓글인지 여부
        private Boolean mine;

    }



}
