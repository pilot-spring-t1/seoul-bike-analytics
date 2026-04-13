package com.metanet.seoulbike.dashboard.service;

import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dashboard.repository.IBikeUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeUsageService {

    private final IBikeUsageRepository bikeUsageRepository;
    
    @Cacheable(value = "monthlyUsage", key = "#year")
    public List<BikeMonthlyUsageDto> getMonthlyUsage(int year) {
        // 실제 데이터 접근은 Repository 인터페이스를 통해 수행
        return bikeUsageRepository.findMonthlyUsageByYear(year);
    }
}