package com.metanet.seoulbike.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.mapper.SeoulBikeMapper;

@RestController
@RequestMapping("/test/bike") // 테스트용 경로 유지
public class SeoulBikeTestController {

	@Autowired
	private SeoulBikeMapper seoulBikeMapper;

	
}