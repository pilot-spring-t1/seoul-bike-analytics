package com.metanet.seoulbike.stats.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.stats.dto.BikeAnalysisDto;
import com.metanet.seoulbike.stats.dto.BikeDayUsageDto;
import com.metanet.seoulbike.stats.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.stats.dto.BikePeriodSearchDto;
import com.metanet.seoulbike.stats.dto.DashboardSummaryDto;
import com.metanet.seoulbike.stats.dto.SeoulBikeDto;

@Mapper
public interface SeoulBikeMapper {

	// 대량의 데이터를 한 번에 넣기 위한 배치 인서트
	int insertBikeBatch(List<SeoulBikeDto> bikeList);

	// 테스트용: 데이터가 잘 들어갔는지 확인하기 위한 전체 조회
	List<SeoulBikeDto> findAllBikeData();

	// 특정 대여소의 데이터만 조회 
	List<SeoulBikeDto> findByOfficeName(String officeName);

	List<BikeMonthlyUsageDto> getMonthlyUsage(int year);

	List<SeoulBikeDto> findByPeriod(BikePeriodSearchDto searchDto);

	List<BikeDayUsageDto> getDayUsage();

	List<Map<String, Object>> getCustomAnalysis(BikeAnalysisDto analysisDto);
	
	// 사용자 대시보드 종합 분석 페이지
	DashboardSummaryDto getDashboardSummary();
	
	// 사용자 대시보드 상세 분석 페이지
	
	List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto);

}