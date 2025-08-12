package com.blog.toy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
    
    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;
    
    // 대댓글인 경우 부모 댓글 ID
    private Long parentId;
}
