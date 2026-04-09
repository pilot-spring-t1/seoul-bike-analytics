package com.metanet.seoulbike.common.log.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.seoulbike.common.log.dto.LogDto;

@Mapper
public interface LogMapper {
    void insertLog(LogDto logDto);
}	