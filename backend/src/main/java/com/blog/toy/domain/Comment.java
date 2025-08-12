package com.blog.toy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 댓글 상태 (ACTIVE, DELETED, REPORTED)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    // 좋아요/싫어요 수
    private Integer likeCount;
    private Integer dislikeCount;

    // 대댓글 기능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    // 게시글과의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 작성자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 댓글 좋아요/싫어요 관계
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentReaction> reactions = new HashSet<>();

    // 댓글 신고 관계
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentReport> reports = new HashSet<>();

    // 댓글 작성자 (기존 호환성을 위해 유지)
    private String author;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CommentStatus.ACTIVE;
        }
        if (likeCount == null) {
            likeCount = 0;
        }
        if (dislikeCount == null) {
            dislikeCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 대댓글 추가
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    // 대댓글 제거
    public void removeReply(Comment reply) {
        replies.remove(reply);
        reply.setParent(null);
    }

    // 좋아요 수 증가
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 싫어요 수 증가
    public void incrementDislikeCount() {
        this.dislikeCount++;
    }

    // 싫어요 수 감소
    public void decrementDislikeCount() {
        if (this.dislikeCount > 0) {
            this.dislikeCount--;
        }
    }

    // 댓글 상태 변경
    public void markAsDeleted() {
        this.status = CommentStatus.DELETED;
    }

    public void markAsReported() {
        this.status = CommentStatus.REPORTED;
    }
}
