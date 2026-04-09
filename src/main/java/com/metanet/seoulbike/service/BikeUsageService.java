package com.metanet.seoulbike.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeUsageService {

    private final SeoulBikeMapper seoulBikeMapper;

    public List<BikeMonthlyUsageDto> getMonthlyUsage(int year) {
        return seoulBikeMapper.getMonthlyUsage(year);
    }
}