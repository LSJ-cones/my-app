package com.blog.toy.dto;

import com.blog.toy.domain.ReactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReactionDto {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long userId;
    private String username;
    private ReactionType type;
    private LocalDateTime createdAt;

    // 게시글 반응 통계
    private Long likeCount;
    private Long dislikeCount;
    private boolean userLiked;
    private boolean userDisliked;
}
