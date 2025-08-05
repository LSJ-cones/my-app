package com.blog.toy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// DTO 클래스는 엔티티와 유사하지만, API 응답에 필요한 필드만 포함
// CommentResponseDto는 댓글 정보를 클라이언트에 전달하기 위한 DTO 클래스입니다.
// 이 클래스는 댓글의 ID, 작성자, 내용, 생성일시를 포함합니다.
public class CommentResponseDto {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
}
