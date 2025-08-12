package com.blog.toy.dto;

import com.blog.toy.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDto {
    private String keyword;                    // 검색 키워드 (제목, 내용)
    private Long categoryId;                   // 카테고리 ID
    private List<String> tagNames;             // 태그 이름들
    private Post.PostStatus status;            // 게시글 상태
    @Builder.Default
    private String sortBy = "createdAt";       // 정렬 기준 (title, createdAt, viewCount, commentCount)
    @Builder.Default
    private String sortDirection = "desc";     // 정렬 방향 (asc, desc)
    @Builder.Default
    private Integer page = 0;                  // 페이지 번호 (0부터 시작)
    @Builder.Default
    private Integer size = 10;                 // 페이지 크기
}
