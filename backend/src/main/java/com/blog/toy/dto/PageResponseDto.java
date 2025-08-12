package com.blog.toy.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageResponseDto<T> {
    private List<T> content; // 실제 데이터
    private int pageNumber; // 현재 페이지 번호
    private int pageSize; // 페이지 크기
    private long totalElements; // 전체 요소 수
    private int totalPages; // 전체 페이지 수
    private boolean hasNext; // 다음 페이지 존재 여부
    private boolean hasPrevious; // 이전 페이지 존재 여부

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}
