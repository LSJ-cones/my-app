package com.blog.toy.controller;

import com.blog.toy.domain.NotificationStatus;
import com.blog.toy.domain.NotificationType;
import com.blog.toy.dto.NotificationResponseDto;
import com.blog.toy.dto.PageRequestDto;
import com.blog.toy.dto.PageResponseDto;
import com.blog.toy.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "페이징을 지원하는 알림 목록을 조회합니다.")
    public ResponseEntity<PageResponseDto<NotificationResponseDto>> getNotifications(
            @ModelAttribute PageRequestDto pageRequestDto) {
        PageResponseDto<NotificationResponseDto> notifications = notificationService.getUserNotifications(pageRequestDto);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/filter")
    @Operation(summary = "알림 필터링 조회", description = "타입, 상태, 날짜별로 알림을 필터링하여 조회합니다.")
    public ResponseEntity<PageResponseDto<NotificationResponseDto>> getFilteredNotifications(
            @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "알림 타입") @RequestParam(required = false) NotificationType type,
            @Parameter(description = "알림 상태") @RequestParam(required = false) NotificationStatus status,
            @Parameter(description = "시작 날짜 (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        PageResponseDto<NotificationResponseDto> notifications = notificationService.getFilteredNotifications(
                pageRequestDto, type, status, startDate, endDate);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회", description = "읽지 않은 알림 목록을 조회합니다.")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications() {
        List<NotificationResponseDto> notifications = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "읽지 않은 알림 개수 조회", description = "읽지 않은 알림의 개수를 조회합니다.")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        long count = notificationService.getUnreadNotificationCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "모든 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "오래된 알림 정리", description = "30일 이상 된 알림을 정리합니다.")
    public ResponseEntity<Void> cleanupOldNotifications() {
        notificationService.cleanupOldNotifications();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-read")
    @Operation(summary = "읽은 알림 일괄 삭제", description = "읽은 알림들을 일괄 삭제합니다.")
    public ResponseEntity<Void> deleteReadNotifications() {
        notificationService.deleteReadNotifications();
        return ResponseEntity.ok().build();
    }
}
