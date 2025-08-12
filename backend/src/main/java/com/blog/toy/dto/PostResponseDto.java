package com.blog.toy.dto;

import com.blog.toy.domain.Post;
import com.blog.toy.dto.category.CategoryResponseDto;
import com.blog.toy.dto.file.FileResponseDto;
import com.blog.toy.dto.tag.TagResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Post.PostStatus status;
    private Integer viewCount;
    private CategoryResponseDto category;
    private List<TagResponseDto> tags;
    private List<FileResponseDto> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDto> comments;
}
