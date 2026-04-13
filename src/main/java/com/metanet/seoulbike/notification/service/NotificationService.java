package com.metanet.seoulbike.notification.service;

import java.util.List;
import com.metanet.seoulbike.notification.dto.NotificationDto;

public interface NotificationService {

    /**
     * 알림 생성 (유니캐스트/브로드캐스트)
     */
    void createNotification(NotificationDto notificationDto);

    /**
     * 특정 회원의 전체 알림 조회
     */
    List<NotificationDto> getNotificationList(Long memberId);

    /**
     * 안 읽은 알림 개수 조회
     */
    int getUnreadCount(Long memberId);

    /**
     * 알림 1건 읽음 처리
     */
    void markAsRead(Long notiId, Long memberId);

    /**
     * 특정 회원의 전체 알림 읽음 처리
     */
    void markAllAsRead(Long memberId);

    /**
     * 알림 삭제
     */
    void deleteNotification(Long notiId, Long memberId);

    /**
     * 오래된 알림 삭제
     */
    void deleteOldNotifications(int days);
}