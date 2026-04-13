package com.metanet.seoulbike.dashboard.repository;

import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import java.util.List;

public interface IBikeUsageRepository {
    // 특정 연도의 월별 이용 현황 조회
    List<BikeMonthlyUsageDto> findMonthlyUsageByYear(int year);
}