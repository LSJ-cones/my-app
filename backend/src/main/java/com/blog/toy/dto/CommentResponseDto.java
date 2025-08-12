package com.blog.toy.dto;

import com.blog.toy.domain.CommentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentStatus status;
    
    // 좋아요/싫어요 수
    private Integer likeCount;
    private Integer dislikeCount;
    
    // 대댓글 관련
    private Long parentId;
    private List<CommentResponseDto> replies;
    
    // 사용자 반응 정보 (현재 사용자가 좋아요/싫어요를 눌렀는지)
    private Boolean isLiked;
    private Boolean isDisliked;
    
    // 작성자 정보
    private String authorName;
    private String authorEmail;
}
