package com.blog.toy.domain;

import jakarta.persistence.*;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 게시글 ID
    private Long id;

    // 댓글 목록을 관리하기 위한 연관관계 설정
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    // 댓글 목록을 초기화하기 위해 @Builder.Default 사용
    @Builder.Default
    // JSON 직렬화 시 무한 참조를 방지하기 위해 @JsonManagedReference 사용
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    // 게시글 제목, 내용, 작성자, 생성일시, 수정일시
    private String title;

    // 게시글 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 게시글 작성자
    private String author;

    // 게시글 생성일시, 수정일시
    private LocalDateTime createdAt;

    // 게시글 수정일시
    private LocalDateTime updatedAt;

    // 게시글 생성 시와 수정 시에 자동으로 현재 시간을 설정하기 위한 메소드
    @PrePersist
    public void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    // 게시글 수정 시에 수정일시를 현재 시간으로 업데이트하기 위한 메소드
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
