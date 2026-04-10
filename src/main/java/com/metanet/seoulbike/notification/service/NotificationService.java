package com.metanet.seoulbike.notification.service;

import java.util.List;

import com.metanet.seoulbike.notification.dto.NotificationDto;

public interface NotificationService {

    // 알림 생성
    void createNotification(NotificationDto notificationDto);

    // 특정 회원의 전체 알림 조회
    List<NotificationDto> selectNotificationsByMemberId(Long memberId);

    // 안 읽은 알림 개수 조회
    int selectUnreadCount(Long memberId);

    // 알림 1건 읽음 처리
    void markAsRead(Long notiId);

    // 특정 회원의 전체 알림 읽음 처리
    void markAllAsRead(Long memberId);

    // 알림 삭제
    void removeNotification(Long notiId);

    // 오래된 알림 삭제
    void removeOldNotifications(int days);
}
