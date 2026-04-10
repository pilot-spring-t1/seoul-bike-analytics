package com.metanet.seoulbike.notification.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate; 
    
    @Override
    public void createNotification(NotificationDto notificationDto) {

        // 1. DB 저장
        notificationMapper.insertNotification(notificationDto);

        // 2. 방금 저장된 알림 다시 조회 (가장 최근 1건)
        List<NotificationDto> list =
            notificationMapper.selectNotificationsByMemberId(notificationDto.getMemberId());

        NotificationDto latest = list.get(0); // 최신순 정렬이라 0번

        // 3. 웹소켓으로 해당 사용자에게 push
        messagingTemplate.convertAndSendToUser(
            notificationDto.getMemberId().toString(), // 사용자 식별자
            "/queue/notifications",
            latest
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> selectNotificationsByMemberId(Long memberId) {
        return notificationMapper.selectNotificationsByMemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public int selectUnreadCount(Long memberId) {
        return notificationMapper.selectUnreadCount(memberId);
    }

    @Override
    public void markAsRead(Long notiId) {
        notificationMapper.updateReadStatus(notiId);
    }

    @Override
    public void markAllAsRead(Long memberId) {
        notificationMapper.updateAllReadStatus(memberId);
    }

    @Override
    public void removeNotification(Long notiId) {
        notificationMapper.deleteNotification(notiId);
    }

    @Override
    public void removeOldNotifications(int days) {
        notificationMapper.deleteOldNotifications(days);
    }
}