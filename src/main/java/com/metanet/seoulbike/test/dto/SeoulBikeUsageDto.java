package com.metanet.seoulbike.test.dto;

import lombok.Data;

@Data
public class SeoulBikeUsageDto {
    private String month;      // YYYY-MM
    private int totalUsage;    // SUM(NUM_OF_USES)
}