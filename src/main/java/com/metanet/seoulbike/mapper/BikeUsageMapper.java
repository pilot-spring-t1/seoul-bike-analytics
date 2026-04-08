package com.metanet.seoulbike.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.metanet.seoulbike.dto.MonthlyUsageDto;

@Mapper
public interface BikeUsageMapper {
    List<MonthlyUsageDto> getMonthlyUsage(@Param("year") int year);
}