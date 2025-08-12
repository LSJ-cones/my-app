package com.blog.toy.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class PageRequestDto {
    private int page = 0; // 페이지 번호 (0부터 시작)
    private int size = 10; // 페이지 크기
    private String sortBy = "createdAt"; // 정렬 기준 필드
    private String sortDirection = "desc"; // 정렬 방향 (asc, desc)

    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }
}
