package com.blog.toy.dto.file;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDto {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
