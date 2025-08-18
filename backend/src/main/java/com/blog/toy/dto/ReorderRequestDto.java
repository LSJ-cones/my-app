package com.blog.toy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderRequestDto {
    private Integer newDisplayOrder;
}
