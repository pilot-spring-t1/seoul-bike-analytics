package com.metanet.seoulbike.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyUsageDto {
    private int month;
    private int totalUses;
}