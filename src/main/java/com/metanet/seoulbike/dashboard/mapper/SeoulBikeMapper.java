package com.metanet.seoulbike.dashboard.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.dashboard.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dashboard.dto.BikeDayUsageDto;
import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dashboard.dto.BikePeriodSearchDto;
import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.dto.DetailSummaryDto;
import com.metanet.seoulbike.dashboard.dto.SeoulBikeDto;

@Mapper
public interface SeoulBikeMapper {

	int insertBikeBatch(List<SeoulBikeDto> bikeList);

	List<SeoulBikeDto> findAllBikeData();

	List<SeoulBikeDto> findByOfficeName(String officeName);

	List<BikeMonthlyUsageDto> getMonthlyUsage(int year);

	List<SeoulBikeDto> findByPeriod(BikePeriodSearchDto searchDto);

	List<BikeDayUsageDto> getDayUsage();

	List<Map<String, Object>> getCustomAnalysis(BikeAnalysisDto analysisDto);
	
	// 사용자 대시보드 종합 분석 페이지
	DashboardSummaryDto getDashboardSummary();
	
	// 사용자 대시보드 상세 분석 페이지
	
	List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto);
	
	List<String> searchOfficeNames(String keyword);  // 대여소 목록
	
	DetailSummaryDto getDetailSummary(BikeAnalysisDto analysisDto); // summary card
	
	List<Map<String, Object>> getGenderUsage(BikeAnalysisDto analysisDto); // 성별 차트
	
	List<Map<String, Object>> getRentalCodeUsage(BikeAnalysisDto analysisDto); // 이용권별 차트

}