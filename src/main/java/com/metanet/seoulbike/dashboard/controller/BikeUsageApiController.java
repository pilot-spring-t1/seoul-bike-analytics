package com.metanet.seoulbike.dashboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dashboard.dto.BikeMonthlyUsageDto;
import com.metanet.seoulbike.dashboard.service.BikeUsageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BikeUsageApiController {

    private final BikeUsageService bikeUsageService;

    @GetMapping("/api/bike/monthly-usage")
    public List<BikeMonthlyUsageDto> getMonthlyUsage(
            @RequestParam(defaultValue = "2025") int year) {
        return bikeUsageService.getMonthlyUsage(year);
    }
}