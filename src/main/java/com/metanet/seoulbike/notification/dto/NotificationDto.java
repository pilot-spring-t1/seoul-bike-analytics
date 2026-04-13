package com.metanet.seoulbike.notification.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDto {
    private Long notiId;
    private Long memberId;    // 수신자
    private Long senderId;    // 발신자
    private String notiType;
    private String message;
    private String isRead;
    private LocalDateTime createdAt;
}