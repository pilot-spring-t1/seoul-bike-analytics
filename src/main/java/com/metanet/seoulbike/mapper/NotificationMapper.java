package com.metanet.seoulbike.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.dto.NotificationDto;

@Mapper
public interface NotificationMapper {
	// 알림 발송 (저장)
	int insertNotification(NotificationDto notiDto);

	// 특정 유저의 알림 목록 조회 (최신순)
	List<NotificationDto> findByUserNo(int userNo);

	// 알림 읽음 처리
	int markAsRead(int notiId);
}