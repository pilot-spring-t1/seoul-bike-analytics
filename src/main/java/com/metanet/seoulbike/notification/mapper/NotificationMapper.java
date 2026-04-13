package com.metanet.seoulbike.notification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.metanet.seoulbike.notification.dto.NotificationDto;

@Mapper
public interface NotificationMapper {

	// 1. 알림 생성
	void insertNotification(NotificationDto notification);

	// 2. 특정 회원의 전체 알림 조회 (최신순) - select로 통일
	List<NotificationDto> selectNotificationsByMemberId(Long memberId);

	// 3. 안 읽은 알림 개수 조회 - select로 통일
	int selectUnreadCount(Long memberId);

	// 4. 알림 단건 읽음 상태 수정
	void updateReadStatus(@Param("notiId") Long notiId);

	// 5. 특정 회원의 모든 알림 일괄 읽음 처리
	void updateAllReadStatus(Long memberId);

	// 6. 특정 알림 삭제
	void deleteNotification(Long notiId);

	// 7. 기간 만료된 알림 일괄 삭제
	void deleteOldNotifications(int days);
	
	// 8. broadcast를 위해 사용자 ID 가져오기
	List<Long> selectAllMemberIdsExceptSender();
}
