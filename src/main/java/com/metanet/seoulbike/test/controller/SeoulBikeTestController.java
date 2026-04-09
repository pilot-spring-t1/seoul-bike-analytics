package com.metanet.seoulbike.test.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dto.BikeDayUsageDto;
import com.metanet.seoulbike.dto.BikePeriodSearchDto;
import com.metanet.seoulbike.dto.SeoulBikeDto;
import com.metanet.seoulbike.mapper.SeoulBikeMapper;

@RestController
@RequestMapping("/test/bike") // 테스트용 경로 유지
public class SeoulBikeTestController {

	@Autowired
	private SeoulBikeMapper seoulBikeMapper;

	@GetMapping("/search")
	public List<SeoulBikeDto> searchByPeriod(BikePeriodSearchDto searchDto) {
		// 로그로 입력값 확인
		System.out.println("검색 시작일: " + searchDto.getStartDate());
		System.out.println("검색 종료일: " + searchDto.getEndDate());

		return seoulBikeMapper.findByPeriod(searchDto);
	}

	@GetMapping("/day-usage")
	public List<BikeDayUsageDto> getDayUsage() {
		return seoulBikeMapper.getDayUsage();
	}

	@GetMapping("/custom-analysis")
	public List<Map<String, Object>> getCustomAnalysis(BikeAnalysisDto dto) {
		// 프론트엔드에서 보낸 파라미터가 dto에 자동 매핑되어 쿼리로 전달됩니다.
		return seoulBikeMapper.getCustomAnalysis(dto);
	}

}