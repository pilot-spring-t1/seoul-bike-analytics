package com.metanet.seoulbike.dashboard.repository;

import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dashboard.mapper.SeoulBikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BikeUsageRepository implements IBikeUsageRepository {

    private final SeoulBikeMapper seoulBikeMapper;

    @Override
    public List<BikeMonthlyUsageDto> findMonthlyUsageByYear(int year) {
        return seoulBikeMapper.getMonthlyUsage(year);
    }
}