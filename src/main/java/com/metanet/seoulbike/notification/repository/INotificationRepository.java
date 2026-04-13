package com.metanet.seoulbike.notification.repository;

import com.metanet.seoulbike.notification.dto.NotificationDto;
import java.util.List;

public interface INotificationRepository {
    // 알림 저장 (단건 및 다건)
    void save(NotificationDto notificationDto);
    void saveAll(List<NotificationDto> notifications);

    // 알림 조회
    List<NotificationDto> findByMemberId(Long memberId);
    int countUnread(Long memberId);

    // 상태 변경 및 삭제
    void updateReadStatus(Long notiId, Long memberId);
    void updateAllReadStatus(Long memberId);
    void delete(Long notiId, Long memberId);
    void deleteExpired(int days);
}