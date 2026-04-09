package com.metanet.seoulbike.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BikeMonthlyUsageDto {
    private int month;
    private int totalUses;
}