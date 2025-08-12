package com.blog.toy.dto;

import com.blog.toy.domain.NotificationStatus;
import com.blog.toy.domain.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String content;
    private String senderName;
    private String senderEmail;
    private Long postId;
    private String postTitle;
    private Long commentId;
    private String commentContent;
    private NotificationStatus status;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
