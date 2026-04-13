package com.metanet.seoulbike.dashboard.repository;

import com.metanet.seoulbike.dashboard.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.dto.DetailSummaryDto;
import java.util.List;
import java.util.Map;

public interface IBikeAnalysisRepository {
    // 연령대별 이용 현황
    List<Map<String, Object>> findAgeGroupUsage(BikeAnalysisDto analysisDto);
    
    // 대시보드 요약 정보 (메인)
    DashboardSummaryDto findDashboardSummary();
    
    // 대여소 명칭 검색
    List<String> findOfficeNames(String keyword);
    
    // 상세 요약 정보 조회
    DetailSummaryDto findDetailSummary(BikeAnalysisDto analysisDto);
    
    // 성별 이용 현황
    List<Map<String, Object>> findGenderUsage(BikeAnalysisDto analysisDto);
    
    // 대여구분별 이용 현황
    List<Map<String, Object>> findRentalCodeUsage(BikeAnalysisDto analysisDto);
}