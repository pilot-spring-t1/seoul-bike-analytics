package com.metanet.seoulbike.dashboard.dto;

import lombok.Data;


// 기간검색에서 Input 값으로 사용
@Data
public class BikePeriodSearchDto {
    private String startDate;
    private String endDate;
}