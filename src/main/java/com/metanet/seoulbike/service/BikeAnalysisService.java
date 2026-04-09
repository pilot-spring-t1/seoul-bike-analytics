package com.metanet.seoulbike.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.metanet.seoulbike.dto.BikeAnalysisDto;
import com.metanet.seoulbike.mapper.SeoulBikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BikeAnalysisService {

    private final SeoulBikeMapper seoulBikeMapper;
    
    public List<Map<String, Object>> getAgeGroupUsage(BikeAnalysisDto analysisDto) {
        return seoulBikeMapper.getAgeGroupUsage(analysisDto);
    }
}