package com.blog.toy.dto;

import com.blog.toy.domain.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReactionDto {
    
    @NotNull(message = "댓글 ID는 필수입니다.")
    private Long commentId;
    
    @NotNull(message = "반응 타입은 필수입니다.")
    private ReactionType type;
}
