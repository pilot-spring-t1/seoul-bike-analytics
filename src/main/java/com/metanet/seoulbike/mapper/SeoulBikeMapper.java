package com.metanet.seoulbike.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dto.BikeDayUsageDto;
import com.metanet.seoulbike.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dto.BikePeriodSearchDto;
import com.metanet.seoulbike.dto.SeoulBikeDto;

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


}