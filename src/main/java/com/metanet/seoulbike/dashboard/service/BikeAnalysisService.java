package com.metanet.seoulbike.dashboard.service;

import com.metanet.seoulbike.dashboard.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.dto.DetailSummaryDto;
import com.metanet.seoulbike.dashboard.repository.IBikeAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BikeAnalysisService {

    private final IBikeAnalysisRepository bikeAnalysisRepository;
    
    public List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto) {
        return bikeAnalysisRepository.findAgeGroupUsage(analysisDto);
    }
    
    @Cacheable(value = "dashboardSummary")
    public DashboardSummaryDto getDashboardSummary() {
        // 서비스는 저장소의 인터페이스를 통해 데이터를 가져옴
        return bikeAnalysisRepository.findDashboardSummary();
    }
    
    public List<String> searchOfficeNames(String keyword) {
        return bikeAnalysisRepository.findOfficeNames(keyword);
    }
    
    public DetailSummaryDto getDetailSummary(BikeAnalysisDto analysisDto) {
        return bikeAnalysisRepository.findDetailSummary(analysisDto);
    }
    
    public List<Map<String, Object>> getGenderUsage(BikeAnalysisDto analysisDto) {
        return bikeAnalysisRepository.findGenderUsage(analysisDto);
    }
    
    public List<Map<String, Object>> getRentalCodeUsage(BikeAnalysisDto analysisDto) {
        return bikeAnalysisRepository.findRentalCodeUsage(analysisDto);
    }
}