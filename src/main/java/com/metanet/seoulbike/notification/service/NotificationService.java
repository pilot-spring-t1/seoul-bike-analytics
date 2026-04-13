package com.metanet.seoulbike.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.member.repository.IMemberRepository; // 인터페이스 사용
import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.repository.INotificationRepository; // 인터페이스 사용

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements INotificationService {

    private final INotificationRepository notificationRepository;
    private final IMemberRepository memberRepository; 
    private final SimpMessagingTemplate messagingTemplate; 
    
    @Override
    public void createNotification(NotificationDto notificationDto) {
        // 1. 전체 발송 (Broadcast)
        if (Long.valueOf(-1L).equals(notificationDto.getMemberId())) {
            processBroadcastNotification(notificationDto);
        } else {
            // 2. 단건 발송 (Unicast)
            processUnicastNotification(notificationDto);
        }
    }

    private void processBroadcastNotification(NotificationDto notificationDto) {
        List<Member> allMembers = memberRepository.findAll(); // Repository 인터페이스 호출
        
        if (allMembers == null || allMembers.isEmpty()) return;

        List<NotificationDto> notificationList = new ArrayList<>();
        for (Member member : allMembers) {
            if (member.getMemberId().equals(notificationDto.getSenderId())) continue; 

            NotificationDto dto = new NotificationDto();
            dto.setMemberId(member.getMemberId());
            dto.setSenderId(notificationDto.getSenderId());
            dto.setNotiType(notificationDto.getNotiType());
            dto.setMessage(notificationDto.getMessage());
            notificationList.add(dto);
        }

        if (!notificationList.isEmpty()) {
            notificationRepository.saveAll(notificationList); // DB 일괄 저장
            
            // 실시간 푸시 전송
            for (NotificationDto targetDto : notificationList) {
                sendWebSocketMessage(targetDto);
            }
        }
    }

    private void processUnicastNotification(NotificationDto notificationDto) {
        notificationRepository.save(notificationDto);
        sendWebSocketMessage(notificationDto);
    }

    private void sendWebSocketMessage(NotificationDto dto) {
        messagingTemplate.convertAndSendToUser(
            dto.getMemberId().toString(),
            "/queue/notifications",
            dto
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationList(Long memberId) {
        return notificationRepository.findByMemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationRepository.countUnread(memberId);
    }

    @Override
    public void markAsRead(Long notiId, Long memberId) {
        notificationRepository.updateReadStatus(notiId, memberId);
    }

    @Override
    public void markAllAsRead(Long memberId) {
        notificationRepository.updateAllReadStatus(memberId);
    }

    @Override
    public void deleteNotification(Long notiId, Long memberId) {
        notificationRepository.delete(notiId, memberId);
    }

    @Override
    public void deleteOldNotifications(int days) {
        notificationRepository.deleteExpired(days);
    }
}