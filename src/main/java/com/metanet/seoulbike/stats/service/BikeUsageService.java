package com.metanet.seoulbike.stats.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.metanet.seoulbike.stats.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.stats.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeUsageService {

    private final SeoulBikeMapper seoulBikeMapper;

    @Cacheable(value = "monthlyUsage", key = "#year")
    public List<BikeMonthlyUsageDto> getMonthlyUsage(int year) {
    	System.out.println("DB 조회 실행: year=" + year); // 테스트용
        return seoulBikeMapper.getMonthlyUsage(year);
    }
}