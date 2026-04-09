package com.metanet.seoulbike.dto;

import lombok.Data;

@Data
public class DetailSummaryDto {
    private double avgDistance;      // 평균 이동거리
    private double avgUsageMinute;   // 평균 이용시간
    private double avgDailyUses;     // 하루 평균 이용 건수
    private double avgSpeed;         // 평균 이동속도 (km/h)
}