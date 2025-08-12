package com.blog.toy.repository;

import com.blog.toy.domain.Notification;
import com.blog.toy.domain.NotificationStatus;
import com.blog.toy.domain.NotificationType;
import com.blog.toy.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 사용자의 알림 조회 (페이징)
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    // 사용자의 읽지 않은 알림 조회
    List<Notification> findByRecipientAndStatusOrderByCreatedAtDesc(User recipient, NotificationStatus status);
    
    // 사용자의 읽지 않은 알림 개수
    long countByRecipientAndStatus(User recipient, NotificationStatus status);
    
    // 특정 게시글의 알림 조회
    List<Notification> findByPostId(Long postId);
    
    // 특정 댓글의 알림 조회
    List<Notification> findByCommentId(Long commentId);
    
    // 알림을 읽음 상태로 변경
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId);
    
    // 사용자의 모든 알림을 읽음 상태로 변경
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = CURRENT_TIMESTAMP WHERE n.recipient.id = :userId")
    void markAllAsRead(@Param("userId") Long userId);
    
    // 오래된 알림 삭제 (30일 이상)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    void deleteOldNotifications(@Param("date") LocalDateTime date);

    // 필터링된 알림 조회 (타입과 상태만)
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient " +
           "AND (:type IS NULL OR n.type = :type) " +
           "AND (:status IS NULL OR n.status = :status) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByFilter(
            @Param("recipient") User recipient,
            @Param("type") NotificationType type,
            @Param("status") NotificationStatus status,
            Pageable pageable);

    // 날짜 범위로 필터링된 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient " +
           "AND n.createdAt >= :startDate " +
           "AND n.createdAt <= :endDate " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByDateRange(
            @Param("recipient") User recipient,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 읽은 알림 일괄 삭제
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipient.id = :userId AND n.status = 'READ'")
    void deleteReadNotifications(@Param("userId") Long userId);
}
