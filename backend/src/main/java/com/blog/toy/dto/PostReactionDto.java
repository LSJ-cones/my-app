package com.blog.toy.dto;

import com.blog.toy.domain.ReactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReactionDto {
    private Long postId;
    private ReactionType type;
}
