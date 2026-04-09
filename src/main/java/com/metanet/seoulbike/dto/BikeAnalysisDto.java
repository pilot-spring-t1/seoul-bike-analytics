package com.metanet.seoulbike.dto;

import lombok.Data;

@Data
public class BikeAnalysisDto {
    // 1. 필터링 (WHERE)
    private String startDate;
    private String endDate;
    private String gender;
    private String ageGroup;
    private String rentalCode; // 정기권/일일권 등
    private String keyword;    // 대여소 이름 검색

    // 2. 분석 기준 (GROUP BY)
    // GENDER(성별), AGE(연령대별), CODE(이용권별)
    private String groupBy;

    // 3. 집계 대상 (SELECT 대상)
    // USES(건수), DISTANCE(거리), CARBON(탄소), EXERCISE(운동량), MINUTE(시간)
    private String targetCol;

    // 4. 집계 함수 및 정렬
    private String calcType;   // SUM, AVG, COUNT
    private String sortBy;     // LABEL, VALUE
    private String sortOrder;  // ASC, DESC
    private Integer limit;     // TOP N
}