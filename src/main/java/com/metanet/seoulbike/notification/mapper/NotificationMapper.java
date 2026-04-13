package com.metanet.seoulbike.notification.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.metanet.seoulbike.notification.dto.NotificationDto;

@Mapper
public interface NotificationMapper {

    // 1. 단건 알림 생성 (Unicast)
    void insertNotification(NotificationDto notification);

    // 2. 알림 일괄 생성 (Broadcast - 서비스의 notificationList 처리용)
    // XML의 foreach 문과 매핑됩니다.
    void insertNotifications(@Param("list") List<NotificationDto> notificationList);

    // 3. 특정 회원의 전체 알림 조회 (최신순)
    List<NotificationDto> selectNotificationsByMemberId(Long memberId);

    // 4. 안 읽은 알림 개수 조회
    int selectUnreadCount(Long memberId);

    // 5. 알림 단건 읽음 처리 (인가 확인을 위해 memberId 포함)
    void updateReadStatus(@Param("notiId") Long notiId, @Param("memberId") Long memberId);

    // 6. 특정 회원의 모든 알림 일괄 읽음 처리
    void updateAllReadStatus(Long memberId);

    // 7. 특정 알림 삭제 (인가 확인을 위해 memberId 포함)
    void deleteNotification(@Param("notiId") Long notiId, @Param("memberId") Long memberId);

    // 8. 기간 만료된 알림 일괄 삭제
    void deleteOldNotifications(int days);

    // ※ 기존 8번(selectAllMemberIdsExceptSender)은 
    // 이제 MemberMapper를 직접 사용하므로 인터페이스에서 삭제해도 무방합니다.
}