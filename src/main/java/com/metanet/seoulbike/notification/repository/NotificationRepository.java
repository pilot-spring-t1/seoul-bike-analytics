package com.metanet.seoulbike.notification.repository;

import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository implements INotificationRepository {

    private final NotificationMapper notificationMapper;

    @Override
    public void save(NotificationDto notificationDto) {
        notificationMapper.insertNotification(notificationDto);
    }

    @Override
    public void saveAll(List<NotificationDto> notifications) {
        notificationMapper.insertNotifications(notifications);
    }

    @Override
    public List<NotificationDto> findByMemberId(Long memberId) {
        return notificationMapper.selectNotificationsByMemberId(memberId);
    }

    @Override
    public int countUnread(Long memberId) {
        return notificationMapper.selectUnreadCount(memberId);
    }

    @Override
    public void updateReadStatus(Long notiId, Long memberId) {
        notificationMapper.updateReadStatus(notiId, memberId);
    }

    @Override
    public void updateAllReadStatus(Long memberId) {
        notificationMapper.updateAllReadStatus(memberId);
    }

    @Override
    public void delete(Long notiId, Long memberId) {
        notificationMapper.deleteNotification(notiId, memberId);
    }

    @Override
    public void deleteExpired(int days) {
        notificationMapper.deleteOldNotifications(days);
    }
}