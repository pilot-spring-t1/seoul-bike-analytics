package com.metanet.seoulbike.dashboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardSummaryDto {
    private long totalUses;
    private double totalDistance;
    private long totalUsageMinute;
}