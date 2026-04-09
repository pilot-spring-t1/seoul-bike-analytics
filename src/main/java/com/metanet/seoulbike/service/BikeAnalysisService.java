package com.metanet.seoulbike.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dto.DetailSummaryDto;
import com.metanet.seoulbike.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeAnalysisService {

    private final SeoulBikeMapper seoulBikeMapper;
    
    //@Cacheable("getAgeGroupUsage")
    public List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getAgeGroupUsage(analysisDto);
    }
    
    //@Cacheable("dashboardSummary")
    public DashboardSummaryDto getDashboardSummary() {
        return seoulBikeMapper.getDashboardSummary();
    }
    
    public List<String> searchOfficeNames(String keyword) {
        return seoulBikeMapper.searchOfficeNames(keyword);
    }
    
    public DetailSummaryDto getDetailSummary(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getDetailSummary(analysisDto);
    }
    
    public List<Map<String, Object>> getGenderUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getGenderUsage(analysisDto);
    }
    
    public List<Map<String, Object>> getRentalCodeUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getRentalCodeUsage(analysisDto);
    }
}