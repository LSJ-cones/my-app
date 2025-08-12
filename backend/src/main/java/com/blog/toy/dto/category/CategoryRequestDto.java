package com.blog.toy.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    
    @NotBlank(message = "카테고리명은 필수입니다.")
    @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다.")
    private String name;
    
    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;
    
    private Integer displayOrder;
    
    private boolean active = true;
}
