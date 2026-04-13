package com.metanet.seoulbike.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.seoulbike.member.mapper.MemberMapper;
import com.metanet.seoulbike.member.model.Member;
import com.metanet.seoulbike.notification.dto.NotificationDto;
import com.metanet.seoulbike.notification.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final MemberMapper memberMapper; 
    private final SimpMessagingTemplate messagingTemplate; 
    
    @Override
    public void createNotification(NotificationDto notificationDto) {
        log.info("Service 알림 생성 시작 - Sender: {}, Type: {}, Target: {}", 
                 notificationDto.getSenderId(), notificationDto.getNotiType(), notificationDto.getMemberId());
        
        // 1. 전체 발송 (Broadcast: memberId == -1)
        if (Long.valueOf(-1L).equals(notificationDto.getMemberId())) {
            log.info("Broadcast 전체 발송 모드 진입 (발신자 제외 로직 실행)");

            List<Member> allMembers = memberMapper.selectAllMembers();
            
            if (allMembers == null || allMembers.isEmpty()) {
                log.warn("Broadcast 발송 대상 회원이 없습니다.");
                return;
            }

            List<NotificationDto> notificationList = new ArrayList<>();
            for (Member member : allMembers) {
                // 발신자(senderId)와 수신자(memberId)가 같으면 알림을 생성하지 않음
                if (member.getMemberId().equals(notificationDto.getSenderId())) {
                    continue; 
                }

                NotificationDto dto = new NotificationDto();
                dto.setMemberId(member.getMemberId());
                dto.setSenderId(notificationDto.getSenderId());
                dto.setNotiType(notificationDto.getNotiType());
                dto.setMessage(notificationDto.getMessage());
                notificationList.add(dto);
            }

            if (!notificationList.isEmpty()) {
                // 2. DB 일괄 저장
                notificationMapper.insertNotifications(notificationList);
                log.info("DB {}건의 알림 일괄 저장 완료", notificationList.size());

                // 3. 실시간 웹소켓 전송
                for (NotificationDto targetDto : notificationList) {
                    messagingTemplate.convertAndSendToUser(
                        targetDto.getMemberId().toString(),
                        "/queue/notifications",
                        targetDto
                    );
                }
                log.info("Websocket 필터링된 회원 대상 푸시 전송 완료");
            }

        } else {
            // 4. 단건 발송 (Unicast)
            log.info("Unicast 단일 발송 모드 진입 - Target: {}", notificationDto.getMemberId());
            
            notificationMapper.insertNotification(notificationDto);
            log.info("DB 단건 알림 저장 완료");

            messagingTemplate.convertAndSendToUser(
                notificationDto.getMemberId().toString(),
                "/queue/notifications",
                notificationDto
            );
            log.info("Websocket 단건 푸시 전송 완료");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationList(Long memberId) {
        log.info("Service 회원 알림 목록 조회 - MemberId: {}", memberId);
        return notificationMapper.selectNotificationsByMemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        int count = notificationMapper.selectUnreadCount(memberId);
        log.info("Service 안 읽은 알림 개수 - MemberId: {}, Count: {}", memberId, count);
        return count;
    }

    @Override
    public void markAsRead(Long notiId, Long memberId) {
        log.info("Service 읽음 처리 요청 - NotiId: {}, MemberId: {}", notiId, memberId);
        notificationMapper.updateReadStatus(notiId, memberId);
    }

    @Override
    public void markAllAsRead(Long memberId) {
        log.info("Service 전체 읽음 처리 요청 - MemberId: {}", memberId);
        notificationMapper.updateAllReadStatus(memberId);
    }

    @Override
    public void deleteNotification(Long notiId, Long memberId) {
        log.info("Service 알림 삭제 요청 - NotiId: {}, MemberId: {}", notiId, memberId);
        notificationMapper.deleteNotification(notiId, memberId);
    }

    @Override
    public void deleteOldNotifications(int days) {
        log.info("Service {}일 지난 알림 일괄 삭제 시작", days);
        notificationMapper.deleteOldNotifications(days);
    }
}