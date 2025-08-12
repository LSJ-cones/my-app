package com.blog.toy.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestDto {
    
    @NotBlank(message = "태그명은 필수입니다.")
    @Size(max = 50, message = "태그명은 50자 이하여야 합니다.")
    private String name;
    
    @Size(max = 200, message = "설명은 200자 이하여야 합니다.")
    private String description;
    
    private boolean active = true;
}
