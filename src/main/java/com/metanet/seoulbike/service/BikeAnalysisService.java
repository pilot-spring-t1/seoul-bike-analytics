package com.metanet.seoulbike.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dto.DashboardSummaryDto;
import com.metanet.seoulbike.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeAnalysisService {

    private final SeoulBikeMapper seoulBikeMapper;
    
    //@Cacheable("getAgeGroupUsage")
    public List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto) {
    	System.out.println("🔥 DB 조회 발생");
        return seoulBikeMapper.getAgeGroupUsage(analysisDto);
    }
    
    //@Cacheable("dashboardSummary")
    public DashboardSummaryDto getDashboardSummary() {
        return seoulBikeMapper.getDashboardSummary();
    }
}