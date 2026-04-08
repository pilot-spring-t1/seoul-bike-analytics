package com.metanet.seoulbike.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dto.MonthlyUsageDto;
import com.metanet.seoulbike.mapper.BikeUsageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeUsageService {

    private final BikeUsageMapper bikeUsageMapper;

    public List<MonthlyUsageDto> getMonthlyUsage(int year) {
        return bikeUsageMapper.getMonthlyUsage(year);
    }
}