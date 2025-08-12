package com.blog.toy.service;

import com.blog.toy.domain.*;
import com.blog.toy.dto.NotificationResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.repository.NotificationRepository;
import com.blog.toy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 댓글 알림 생성
    public void createCommentNotification(Comment comment, Post post) {
        User commentAuthor = comment.getUser();
        
        // 게시글 작성자 정보를 author 필드로 조회
        User postAuthor = userRepository.findByUsername(post.getAuthor())
                .orElse(null);
        
        // 게시글 작성자를 찾을 수 없거나 자신의 게시글에 댓글을 달면 알림 생성하지 않음
        if (postAuthor == null || commentAuthor.getId().equals(postAuthor.getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.COMMENT)
                .title("새로운 댓글")
                .content(commentAuthor.getUsername() + "님이 회원님의 게시글에 댓글을 남겼습니다.")
                .recipient(postAuthor)
                .sender(commentAuthor)
                .post(post)
                .comment(comment)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(postAuthor.getId(), convertToDto(savedNotification));
    }

    // 대댓글 알림 생성
    public void createReplyNotification(Comment reply, Comment parentComment) {
        User replyAuthor = reply.getUser();
        User parentAuthor = parentComment.getUser();
        
        // 자신의 댓글에 대댓글을 달면 알림 생성하지 않음
        if (replyAuthor.getId().equals(parentAuthor.getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.REPLY)
                .title("새로운 대댓글")
                .content(replyAuthor.getUsername() + "님이 회원님의 댓글에 대댓글을 남겼습니다.")
                .recipient(parentAuthor)
                .sender(replyAuthor)
                .post(reply.getPost())
                .comment(reply)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(parentAuthor.getId(), convertToDto(savedNotification));
    }

    // 좋아요 알림 생성
    public void createLikeNotification(CommentReaction reaction) {
        User reactionUser = reaction.getUser();
        User commentAuthor = reaction.getComment().getUser();
        
        // 자신의 댓글에 좋아요를 누르면 알림 생성하지 않음
        if (reactionUser.getId().equals(commentAuthor.getId())) {
            return;
        }

        String reactionText = reaction.getType() == ReactionType.LIKE ? "좋아요" : "싫어요";

        Notification notification = Notification.builder()
                .type(NotificationType.LIKE)
                .title("새로운 " + reactionText)
                .content(reactionUser.getUsername() + "님이 회원님의 댓글에 " + reactionText + "를 눌렀습니다.")
                .recipient(commentAuthor)
                .sender(reactionUser)
                .post(reaction.getComment().getPost())
                .comment(reaction.getComment())
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(commentAuthor.getId(), convertToDto(savedNotification));
    }

    // 게시글 좋아요 알림 생성
    public void createPostLikeNotification(PostReaction reaction) {
        User reactionUser = reaction.getUser();
        
        // 게시글 작성자 정보를 author 필드로 조회
        User postAuthor = userRepository.findByUsername(reaction.getPost().getAuthor())
                .orElse(null);
        
        // 게시글 작성자를 찾을 수 없거나 자신의 게시글에 좋아요를 누르면 알림 생성하지 않음
        if (postAuthor == null || reactionUser.getId().equals(postAuthor.getId())) {
            log.info("게시글 좋아요 알림 생성 건너뜀: postAuthor={}, reactionUser={}", 
                    reaction.getPost().getAuthor(), reactionUser.getUsername());
            return;
        }

        String reactionText = reaction.getType() == ReactionType.LIKE ? "좋아요" : "싫어요";

        Notification notification = Notification.builder()
                .type(NotificationType.POST_LIKE)
                .title("새로운 게시글 " + reactionText)
                .content(reactionUser.getUsername() + "님이 회원님의 게시글에 " + reactionText + "를 눌렀습니다.")
                .recipient(postAuthor)
                .sender(reactionUser)
                .post(reaction.getPost())
                .comment(null)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(postAuthor.getId(), convertToDto(savedNotification));
        
        log.info("게시글 좋아요 알림 생성 완료: 알림 ID={}, 수신자={}, 발신자={}", 
                savedNotification.getId(), postAuthor.getUsername(), reactionUser.getUsername());
    }

    // 게시글 업데이트 알림 생성
    public void createPostUpdateNotification(Post post) {
        // 게시글 작성자 정보를 author 필드로 조회
        User postAuthor = userRepository.findByUsername(post.getAuthor())
                .orElse(null);
        
        // 게시글 작성자를 찾을 수 없으면 알림 생성하지 않음
        if (postAuthor == null) {
            return;
        }
        
        // 게시글 작성자에게 업데이트 알림
        Notification notification = Notification.builder()
                .type(NotificationType.POST_UPDATE)
                .title("게시글 업데이트")
                .content("회원님의 게시글이 업데이트되었습니다.")
                .recipient(postAuthor)
                .post(post)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(postAuthor.getId(), convertToDto(savedNotification));
    }

    // 시스템 알림 생성
    public void createSystemNotification(User recipient, String title, String content) {
        Notification notification = Notification.builder()
                .type(NotificationType.SYSTEM)
                .title(title)
                .content(content)
                .recipient(recipient)
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        
        // 실시간 알림 전송
        sendRealTimeNotification(recipient.getId(), convertToDto(savedNotification));
    }

    // 사용자의 알림 조회 (페이징)
    public PageResponseDto<NotificationResponseDto> getUserNotifications(PageRequestDto pageRequestDto) {
        User currentUser = getCurrentUser();
        Page<Notification> notificationPage = notificationRepository.findByRecipientOrderByCreatedAtDesc(
                currentUser, pageRequestDto.toPageable());
        
        Page<NotificationResponseDto> responsePage = notificationPage.map(this::convertToDto);
        return new PageResponseDto<>(responsePage);
    }

    // 읽지 않은 알림 조회
    public List<NotificationResponseDto> getUnreadNotifications() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationRepository.findByRecipientAndStatusOrderByCreatedAtDesc(
                currentUser, NotificationStatus.UNREAD);
        
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 읽지 않은 알림 개수 조회
    public long getUnreadNotificationCount() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByRecipientAndStatus(currentUser, NotificationStatus.UNREAD);
    }

    // 알림을 읽음 상태로 변경
    public void markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // 자신의 알림만 읽음 처리 가능
            if (notification.getRecipient().getId().equals(currentUser.getId())) {
                notificationRepository.markAsRead(notificationId);
            }
        }
    }

    // 모든 알림을 읽음 상태로 변경
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        notificationRepository.markAllAsRead(currentUser.getId());
    }

    // 알림 삭제
    public void deleteNotification(Long notificationId) {
        User currentUser = getCurrentUser();
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // 자신의 알림만 삭제 가능
            if (notification.getRecipient().getId().equals(currentUser.getId())) {
                notificationRepository.delete(notification);
            }
        }
    }

    // 오래된 알림 정리 (30일 이상)
    public void cleanupOldNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteOldNotifications(thirtyDaysAgo);
        log.info("30일 이상 된 알림을 정리했습니다.");
    }

    // 필터링된 알림 조회
    public PageResponseDto<NotificationResponseDto> getFilteredNotifications(
            PageRequestDto pageRequestDto, 
            NotificationType type, 
            NotificationStatus status, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        Pageable pageable = pageRequestDto.toPageable();
        
        Page<Notification> notificationPage;
        
        // 날짜 범위가 지정된 경우
        if (startDate != null && endDate != null) {
            notificationPage = notificationRepository.findByDateRange(
                    currentUser, startDate, endDate, pageable);
        } else {
            // 타입과 상태만으로 필터링
            notificationPage = notificationRepository.findByFilter(
                    currentUser, type, status, pageable);
        }
        
        Page<NotificationResponseDto> responsePage = notificationPage.map(this::convertToDto);
        return new PageResponseDto<>(responsePage);
    }

    // 읽은 알림 일괄 삭제
    public void deleteReadNotifications() {
        User currentUser = getCurrentUser();
        notificationRepository.deleteReadNotifications(currentUser.getId());
        log.info("읽은 알림들을 일괄 삭제했습니다. 사용자 ID: {}", currentUser.getId());
    }

    // 실시간 알림 전송
    private void sendRealTimeNotification(Long userId, NotificationResponseDto notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );
            log.info("실시간 알림 전송 완료: 사용자 ID {}, 알림 ID {}", userId, notification.getId());
        } catch (Exception e) {
            log.error("실시간 알림 전송 실패: 사용자 ID {}, 오류: {}", userId, e.getMessage());
        }
    }

    // DTO 변환
    private NotificationResponseDto convertToDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .senderName(notification.getSender() != null ? notification.getSender().getUsername() : null)
                .senderEmail(notification.getSender() != null ? notification.getSender().getEmail() : null)
                .postId(notification.getPost() != null ? notification.getPost().getId() : null)
                .postTitle(notification.getPost() != null ? notification.getPost().getTitle() : null)
                .commentId(notification.getComment() != null ? notification.getComment().getId() : null)
                .commentContent(notification.getComment() != null ? notification.getComment().getContent() : null)
                .status(notification.getStatus())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    // 현재 사용자 조회
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            // 임시로 admin 사용자 반환 (테스트용)
            return userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
