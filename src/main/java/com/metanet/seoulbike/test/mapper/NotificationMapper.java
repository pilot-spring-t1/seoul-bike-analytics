package com.metanet.seoulbike.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.test.dto.NotificationDto;

@Mapper
public interface NotificationMapper {
	// 알림 발송 (저장)
	int insertNotification(NotificationDto notiDto);

	// 특정 멤버의 알림 목록 조회
	List<NotificationDto> findByMemberId(Long memberId);

	// 알림 읽음 처리
	int markAsRead(Long notiId);
}