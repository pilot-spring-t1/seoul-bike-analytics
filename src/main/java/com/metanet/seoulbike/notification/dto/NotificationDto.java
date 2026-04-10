package com.metanet.seoulbike.notification.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDto {
	private Long notiId;
	private Long memberId;
	private String notiType; // 예: BOARD, COMMENT, SYSTEM, LIKE
	private String message;
	private String isRead; // 'Y' or 'N'
	private LocalDateTime createdAt;
}
