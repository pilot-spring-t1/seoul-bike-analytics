package com.metanet.seoulbike.notification.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.metanet.seoulbike.notification.dto.NotificationDto;

@Mapper
public interface NotificationMapper {

    /**
     * 1. 단건 알림 생성 (Unicast)
     * DTO 내부의 senderId, memberId 등을 사용합니다.
     */
    void insertNotification(NotificationDto notification);

    /**
     * 2. 알림 일괄 생성 (Broadcast)
     * XML의 foreach 문을 통해 리스트 내 모든 DTO의 senderId를 포함하여 저장합니다.
     */
    void insertNotifications(@Param("list") List<NotificationDto> notificationList);

    /**
     * 3. 특정 회원의 전체 알림 조회 (최신순)
     * SENDER_ID를 포함한 전체 정보를 가져옵니다.
     */
    List<NotificationDto> selectNotificationsByMemberId(Long memberId);

    /**
     * 4. 안 읽은 알림 개수 조회
     */
    int selectUnreadCount(Long memberId);

    /**
     * 5. 알림 단건 읽음 처리
     * @Param을 통해 XML에서 #{notiId}, #{memberId}로 접근합니다.
     */
    void updateReadStatus(@Param("notiId") Long notiId, @Param("memberId") Long memberId);

    /**
     * 6. 특정 회원의 모든 알림 일괄 읽음 처리
     */
    void updateAllReadStatus(Long memberId);

    /**
     * 7. 특정 알림 삭제
     */
    void deleteNotification(@Param("notiId") Long notiId, @Param("memberId") Long memberId);

    /**
     * 8. 기간 만료된 알림 일괄 삭제 (배치 작업용)
     */
    void deleteOldNotifications(int days);
}