package com.metanet.seoulbike.test.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private int notiId;
    private int userNo;
    private String notiType;
    private String message;
    private String isRead;
    private LocalDateTime createDate;
    
    // 조인 시 유저 이름을 함께 표시하기 위해 추가
    private String userName; 
}