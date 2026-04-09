package com.metanet.seoulbike.stats.dto;

import lombok.Data;

@Data
public class BikeDayUsageDto {
    private String dayOfWeek;  // 요일 이름
    private int totalUsage;    // 해당 요일의 이용 합계
}