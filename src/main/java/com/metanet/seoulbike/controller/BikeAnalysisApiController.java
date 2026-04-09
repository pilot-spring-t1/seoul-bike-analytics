package com.metanet.seoulbike.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.service.BikeAnalysisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BikeAnalysisApiController {

    private final BikeAnalysisService bikeAnalysisService;
    
    @GetMapping("/api/analysis/age")
    public List<Map<String, Object>> getAgeGroupAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getAgeGroupUsage(analysisDto);
    }
}