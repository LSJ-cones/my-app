package com.blog.toy.domain;

public enum NotificationType {
    COMMENT,        // 댓글 알림
    REPLY,          // 대댓글 알림
    LIKE,           // 댓글 좋아요 알림
    POST_LIKE,      // 게시글 좋아요 알림
    POST_UPDATE,    // 게시글 업데이트 알림
    SYSTEM          // 시스템 알림
}
