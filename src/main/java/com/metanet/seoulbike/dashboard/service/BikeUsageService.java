package com.metanet.seoulbike.dashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dashboard.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeUsageService {

    private final SeoulBikeMapper seoulBikeMapper;

    public List<BikeMonthlyUsageDto> getMonthlyUsage(int year) {
        return seoulBikeMapper.getMonthlyUsage(year);
    }
}