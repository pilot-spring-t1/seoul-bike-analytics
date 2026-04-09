package com.metanet.seoulbike.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dto.BikeDayUsageDto;
import com.metanet.seoulbike.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dto.BikePeriodSearchDto;
import com.metanet.seoulbike.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dto.SeoulBikeDto;

@Mapper
public interface SeoulBikeMapper {

	int insertBikeBatch(List<SeoulBikeDto> bikeList);

	List<SeoulBikeDto> findAllBikeData();

	List<SeoulBikeDto> findByOfficeName(String officeName);

	List<BikeMonthlyUsageDto> getMonthlyUsage(int year);

	List<SeoulBikeDto> findByPeriod(BikePeriodSearchDto searchDto);

	List<BikeDayUsageDto> getDayUsage();

	List<Map<String, Object>> getCustomAnalysis(BikeAnalysisDto analysisDto);
	
	DashboardSummaryDto getDashboardSummary();
	
	List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto);

}