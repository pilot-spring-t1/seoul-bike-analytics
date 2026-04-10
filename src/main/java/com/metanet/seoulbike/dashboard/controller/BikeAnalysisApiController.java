package com.metanet.seoulbike.dashboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.seoulbike.dashboard.dto.BikeAnalysisDto;
import com.metanet.seoulbike.dashboard.dto.DetailSummaryDto;
import com.metanet.seoulbike.dashboard.service.BikeAnalysisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class BikeAnalysisApiController {

    private final BikeAnalysisService bikeAnalysisService;
    
    @GetMapping("/age")
    public List<Map<String, Object>> getAgeGroupAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getAgeGroupUsage(analysisDto);
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<String> searchOfficeNames(@RequestParam String keyword) {
        return bikeAnalysisService.searchOfficeNames(keyword);
    }
    
    @GetMapping("/summary")
    public DetailSummaryDto getDetailSummary(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getDetailSummary(analysisDto);
    }
    
    @GetMapping("/gender")
    public List<Map<String, Object>> getGenderAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getGenderUsage(analysisDto);
    }
    
    @GetMapping("/ticket")
    public List<Map<String, Object>> getRentalCodeAnalysis(@ModelAttribute BikeAnalysisDto analysisDto) {
        return bikeAnalysisService.getRentalCodeUsage(analysisDto);
    }
}