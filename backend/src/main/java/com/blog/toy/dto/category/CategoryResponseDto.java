package com.blog.toy.dto.category;

import com.blog.toy.domain.Category;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private boolean active;
    private Long parentId;
    private String parentName;
    private String fullPath;
    private Long postCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
