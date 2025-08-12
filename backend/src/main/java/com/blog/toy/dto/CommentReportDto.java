package com.blog.toy.dto;

import com.blog.toy.domain.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReportDto {
    
    @NotNull(message = "댓글 ID는 필수입니다.")
    private Long commentId;
    
    @NotNull(message = "신고 사유는 필수입니다.")
    private ReportReason reason;
    
    private String description; // 신고 상세 내용
}
