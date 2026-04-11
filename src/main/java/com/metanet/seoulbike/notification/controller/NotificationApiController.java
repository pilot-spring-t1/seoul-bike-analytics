package com.metanet.seoulbike.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/{memberId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long memberId) {
        return ResponseEntity.ok(notificationService.selectNotificationsByMemberId(memberId));
    }

    @GetMapping("/{memberId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long memberId) {
        return ResponseEntity.ok(notificationService.selectUnreadCount(memberId));
    }

    @PatchMapping("/{notiId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notiId) {
        notificationService.markAsRead(notiId);
        return ResponseEntity.ok("읽음 처리 완료");
    }

    @DeleteMapping("/{notiId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notiId) {
        notificationService.removeNotification(notiId);
        return ResponseEntity.ok("삭제 완료");
    }

    @PostMapping("/test")
    public ResponseEntity<String> createTestNotification(@RequestBody NotificationDto dto) {
        notificationService.createNotification(dto);
        return ResponseEntity.ok("알림 생성 완료");
    }
}