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
    private Long id;

    // 게시글 제목, 내용, 작성자, 생성일시, 수정일시
    private String title;

    // 게시글 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 게시글 작성자
    private String author;
    
    // 게시글 작성자 ID
    @Column(name = "author_id")
    private Long authorId;

    // 게시글 상태 (DRAFT, PUBLISHED, ARCHIVED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    // 조회수
    @Column(name = "view_count")
    private Integer viewCount = 0;

    // 좋아요 수
    @Column(name = "like_count")
    private Integer likeCount = 0;

    // 싫어요 수
    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;

    // 카테고리와의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 태그와의 관계 (N:M)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

               // 댓글 목록을 관리하기 위한 연관관계 설정
           @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
           @Builder.Default
           @JsonManagedReference
           private List<Comment> comments = new ArrayList<>();

           // 파일 목록을 관리하기 위한 연관관계 설정
           @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
           @Builder.Default
           private List<File> files = new ArrayList<>();

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

    // 조회수 증가 메소드
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }

    // 좋아요 수 증가 메소드
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null) ? 1 : this.likeCount + 1;
    }

    // 좋아요 수 감소 메소드
    public void decrementLikeCount() {
        this.likeCount = (this.likeCount == null || this.likeCount <= 0) ? 0 : this.likeCount - 1;
    }

    // 싫어요 수 증가 메소드
    public void incrementDislikeCount() {
        this.dislikeCount = (this.dislikeCount == null) ? 1 : this.dislikeCount + 1;
    }

    // 싫어요 수 감소 메소드
    public void decrementDislikeCount() {
        this.dislikeCount = (this.dislikeCount == null || this.dislikeCount <= 0) ? 0 : this.dislikeCount - 1;
    }

    // 태그 추가 메소드
    public void addTag(Tag tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            tag.getPosts().add(this);
        }
    }

    // 태그 제거 메소드
    public void removeTag(Tag tag) {
        if (this.tags.remove(tag)) {
            tag.getPosts().remove(this);
        }
    }

    // 게시글 상태 열거형
    public enum PostStatus {
        DRAFT,      // 임시저장
        PUBLISHED,  // 발행
        ARCHIVED    // 보관
    }
}
