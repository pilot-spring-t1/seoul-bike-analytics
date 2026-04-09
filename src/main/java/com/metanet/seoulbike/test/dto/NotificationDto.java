package com.metanet.seoulbike.test.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
	private Long notiId;
	private Long memberId;
	private String notiType;
	private String message;
	private String isRead;
	private LocalDateTime createdAt;

	private String name;
}