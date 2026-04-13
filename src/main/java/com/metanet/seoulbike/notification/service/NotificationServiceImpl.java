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

        // 브로드캐스트: memberId == -1
        if (Long.valueOf(-1L).equals(notificationDto.getMemberId())) {

            List<Long> memberIds = notificationMapper.selectAllMemberIdsExceptSender();

            for (Long memberId : memberIds) {
                NotificationDto dto = new NotificationDto();
                dto.setMemberId(memberId);
                dto.setNotiType(notificationDto.getNotiType());
                dto.setMessage(notificationDto.getMessage());

                // 1. DB 저장
                notificationMapper.insertNotification(dto);

                // 2. 방금 저장된 알림 다시 조회
                List<NotificationDto> list =
                    notificationMapper.selectNotificationsByMemberId(memberId);

                NotificationDto latest = list.get(0);

                // 3. 웹소켓 전송
                messagingTemplate.convertAndSendToUser(
                    memberId.toString(),
                    "/queue/notifications",
                    latest
                );
            }

        } else {
            // 유니캐스트
            notificationMapper.insertNotification(notificationDto);

            List<NotificationDto> list =
                notificationMapper.selectNotificationsByMemberId(notificationDto.getMemberId());

            NotificationDto latest = list.get(0);

            messagingTemplate.convertAndSendToUser(
                notificationDto.getMemberId().toString(),
                "/queue/notifications",
                latest
            );
        }
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