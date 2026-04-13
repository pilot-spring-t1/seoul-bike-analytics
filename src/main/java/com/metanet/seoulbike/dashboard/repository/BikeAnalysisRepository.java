package com.metanet.seoulbike.dashboard.repository;

import com.metanet.seoulbike.dashboard.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dashboard.dto.DashboardSummaryDto;
import com.metanet.seoulbike.dashboard.dto.DetailSummaryDto;
import com.metanet.seoulbike.dashboard.mapper.SeoulBikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BikeAnalysisRepository implements IBikeAnalysisRepository {

    private final SeoulBikeMapper seoulBikeMapper;

    @Override
    public List<Map<String, Object>> findAgeGroupUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getAgeGroupUsage(analysisDto);
    }

    @Override
    public DashboardSummaryDto findDashboardSummary() {
        return seoulBikeMapper.getDashboardSummary();
    }

    @Override
    public List<String> findOfficeNames(String keyword) {
        return seoulBikeMapper.searchOfficeNames(keyword);
    }

    @Override
    public DetailSummaryDto findDetailSummary(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getDetailSummary(analysisDto);
    }

    @Override
    public List<Map<String, Object>> findGenderUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getGenderUsage(analysisDto);
    }

    @Override
    public List<Map<String, Object>> findRentalCodeUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getRentalCodeUsage(analysisDto);
    }
}