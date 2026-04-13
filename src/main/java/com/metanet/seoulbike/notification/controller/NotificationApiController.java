package com.metanet.seoulbike.notification.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회 (본인 또는 관리자)
     */
    @PreAuthorize("authentication.principal.memberId == #memberId or hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable("memberId") Long memberId,
                                                                 @AuthenticationPrincipal Member member) {
        log.info("=> [API GET] 알림 목록 조회 요청 - Target MemberId: {}, Request Principal: {}", 
                 memberId, (member != null ? member.getMemberId() : "Anonymous"));
        
        return ResponseEntity.ok(notificationService.selectNotificationsByMemberId(memberId));
    }

    /**
     * 안 읽은 알림 개수 조회
     */
    @PreAuthorize("authentication.principal.memberId == #memberId or hasRole('ADMIN')")
    @GetMapping("/{memberId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(notificationService.selectUnreadCount(memberId));
    }

    /**
     * 알림 읽음 처리
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{notiId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("notiId") Long notiId, 
                                           @AuthenticationPrincipal Member member) {
        log.info("=> [API PATCH] 알림 읽음 처리 - NotiId: {}, MemberId: {}", notiId, member.getMemberId());
        notificationService.markAsRead(notiId, member.getMemberId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 알림 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{notiId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notiId") Long notiId, 
                                                   @AuthenticationPrincipal Member member) {
        log.info("=> [API DELETE] 알림 삭제 - NotiId: {}, MemberId: {}", notiId, member.getMemberId());
        notificationService.removeNotification(notiId, member.getMemberId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 시스템 알림 발송 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send")
    public ResponseEntity<String> createNotification(@RequestBody NotificationDto dto,
                                                     @AuthenticationPrincipal Member admin) {
        
        // 1. 보안 체크: 인증된 관리자 정보가 없으면 거절
        if (admin == null) {
            log.error("=> [API POST] 발송 실패: 인증된 관리자 정보가 없음");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        log.info("=> [API POST] 시스템 알림 발송 시작 - Admin: {}({}), Type: {}", 
                 admin.getName(), admin.getMemberId(), dto.getNotiType());
        
        try {
            // 2. 인증 객체에서 관리자 ID(SenderId)를 추출하여 DTO에 세팅
            dto.setSenderId(admin.getMemberId());
            
            // 3. 서비스 호출 (Service에서 SenderId를 사용하여 자기 자신 제외 로직 수행)
            notificationService.createNotification(dto);
            
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("=> 발송 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("fail: " + e.getMessage());
        }
    }
}