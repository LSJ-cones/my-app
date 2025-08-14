package com.blog.toy.dto;

import com.blog.toy.domain.Post;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDto {
    private String title;
    private String content;
    private String author;
    private Long categoryId;
    private List<String> tagNames;
    private List<Long> fileIds;
    private Post.PostStatus status = Post.PostStatus.DRAFT;
}
