package com.metanet.seoulbike.stats.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.stats.dto.BikeAnalysisDto;
import com.metanet.seoulbike.stats.dto.DetailSummaryDto;
import com.metanet.seoulbike.stats.service.BikeAnalysisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BikeAnalysisApiController {

    private final BikeAnalysisService bikeAnalysisService;
    
    @GetMapping("/api/analysis/age")
    public List<Map<String, Object>> getAgeGroupAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getAgeGroupUsage(analysisDto);
    }
    
    @GetMapping("/api/offices/search")
    @ResponseBody
    public List<String> searchOfficeNames(@RequestParam String keyword) {
        return bikeAnalysisService.searchOfficeNames(keyword);
    }
    
    @GetMapping("/api/analysis/summary")
    public DetailSummaryDto getDetailSummary(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getDetailSummary(analysisDto);
    }
    
    @GetMapping("/api/analysis/gender")
    public List<Map<String, Object>> getGenderAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getGenderUsage(analysisDto);
    }
    
    @GetMapping("/api/analysis/ticket")
    public List<Map<String, Object>> getRentalCodeAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getRentalCodeUsage(analysisDto);
    }
}