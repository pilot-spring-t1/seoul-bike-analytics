package com.metanet.seoulbike.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.test.dto.SeoulBikeUsageDto;
import com.metanet.seoulbike.test.mapper.SeoulBikeMapper;

@RestController
@RequestMapping("/test/bike") // 테스트용 경로 유지
public class SeoulBikeTestController {

	@Autowired
	private SeoulBikeMapper seoulBikeMapper;

	/**
	 * 월별 이용 횟수 조회 테스트 API URL: GET http://localhost:8080/test/bike/monthly-usage
	 */
	@GetMapping("/monthly-usage")
	public List<SeoulBikeUsageDto> getMonthlyUsage() {
		// 데이터가 너무 많을 경우를 대비해 실행 시간 측정을 곁들이면 좋습니다.
		long start = System.currentTimeMillis();

		List<SeoulBikeUsageDto> result = seoulBikeMapper.getMonthlyUsage();

		long end = System.currentTimeMillis();
		System.out.println("조회 소요 시간: " + (end - start) + "ms");

		return result;
	}
}